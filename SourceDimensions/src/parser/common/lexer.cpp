#include "lexer.h"

#include "unicode/ucnv.h"
#include "unicode/ucnv_err.h"
#include "unicode/uenum.h"

#include "exceptions.h"

#define ZERO_UCHAR "\\u0"


static const char *defaultEncoding = "UTF-8";



CLexer::CLexer()
{
	mContext.eof = false;
	mContext.cur = 0;
	mContext.target_frame_id = 0;
	m_pStream = NULL;
	m_pConverter = NULL;
	mContext.line = 0;
	mContext.tab_num = 0;
	m_nCurBufIndex = 0;
	m_bReuseBuffer = false;
}


void CLexer::SetInputStream(istream &istr)
{
	UErrorCode err = U_ZERO_ERROR;
		
	m_pConverter = NULL;
	m_pStream = &istr;
	ReadBlock();
	m_chEncoding = ucnv_detectUnicodeSignature(m_chSourceBuf, mContext.source_len, (int32_t*)&m_nSignatureLength, &err);

	if (U_SUCCESS(err))
	{
		if (m_chEncoding == NULL)
			m_chEncoding = defaultEncoding;	

		m_pConverter = ucnv_open(m_chEncoding, &err);

		if (U_FAILURE(err))
			throw new CInputConversionErrorException();
 
		mContext.source_bound = m_chSourceBuf;	
		m_pTargetBuf = m_chTargetBuf[0];
		m_ppTargetBound = &(m_pTargetBound[0]);
		*m_ppTargetBound = m_pTargetBuf;
		mContext.prev_bound = mContext.source_bound;

		ucnv_toUnicode(m_pConverter, m_ppTargetBound, m_pTargetBuf + CONV_BUF_SIZE/ucnv_getMinCharSize(m_pConverter),
			(const char**)&mContext.source_bound, m_chSourceBuf + mContext.source_len, NULL, m_bLastBlock, &err);

		if (err == U_BUFFER_OVERFLOW_ERROR)
			mContext.buf_overflow = true;
		else
		{
			if (U_FAILURE(err))
				throw new CInputConversionErrorException();
			else
				mContext.buf_overflow = false;
		}

		if (m_pTargetBuf[0] == 0xFEFF)
			mContext.target_cur = 1;
		else
			mContext.target_cur = 0;
	
		mContext.eof = false;
	}
	else
	{
		throw new CInputConversionErrorException();
	}

	InitLexer();
}


bool CLexer::PreProcess()
{
	return false;
}


void CLexer::PostProcess()
{
}


istream *CLexer::GetInputStream()
{
	return m_pStream;
}


int CLexer::GetCurToken() const
{
	return mContext.token;
}


const char *CLexer::GetEncoding() const
{
	return m_chEncoding;
}


int CLexer::GetNextToken()
{
	mContext.token_value = "";
	mContext.value_buffer = "";

	SkipWhiteSpaces();

	mContext.left = mContext.cur;

	if (!PreProcess())
	{
		ReadToken();
		PostProcess();
	}	

	mContext.right = mContext.cur;

	return mContext.token;
}


int CLexer::LookAhead(unsigned int pos)
{
	if (pos == 0)
		return mContext.token;
	else
	{
		Mark();

		for (int i = pos; i > 0; i--)
			GetNextToken();

		int token = mContext.token;

		Rollback();

		return token;
	}
}


void CLexer::Mark()
{
	LEX_CONTEXT ctx = mContext;
	long cur = m_pStream->tellg();

	if (cur == -1)
		m_pStream->seekg(-1, ios_base::cur);

	ctx.pos = m_pStream->tellg();

	if (mContext.target_cur >= 0) ctx.pos -= mContext.source_len;

	if (ctx.pos < 0) ctx.pos = 0;

	mCtxStack.push(ctx);
}


void CLexer::Unmark()
{
	if (!mCtxStack.empty())
		mCtxStack.pop();
}


void CLexer::Rollback()
{
	if (!mCtxStack.empty())
	{
		int frame_id = mContext.target_frame_id;

		mContext = mCtxStack.top();
		mCtxStack.pop();

		if (mContext.target_frame_id != frame_id/*mContext.target_cur >= 0*/)
		{
			UErrorCode err = U_ZERO_ERROR;

			m_pStream->seekg(mContext.pos, ios_base::beg);

			ReadBlock();
			*m_ppTargetBound = m_pTargetBuf;
			mContext.source_bound = mContext.prev_bound;

			ucnv_toUnicode(m_pConverter, m_ppTargetBound, m_pTargetBuf + CONV_BUF_SIZE/ucnv_getMinCharSize(m_pConverter),
				(const char**)&mContext.source_bound, m_chSourceBuf + mContext.source_len, NULL, false, &err);

			if (U_FAILURE(err))
				throw new CInputConversionErrorException();
		}

		while (!mSubstStack.empty() && mSubstStack.top().cur - mSubstStack.top().len  >= mContext.cur)
			mSubstStack.pop();
	}
}



long CLexer::GetLeftPos() const
{
	return mContext.left;
}


long CLexer::GetRightPos() const
{
	return mContext.right;
}


long CLexer::GetLineNum() const
{
	return (mContext.line + 1);
}


UChar32 CLexer::ReadChar(bool ignore)
{
	mContext.ch = GetNextChar();

	if (!mContext.eof)
	{
		if (mContext.ch == '\r')
		{
			mContext.ch = GetNextChar();

			if (mContext.ch == '\n')
			{
				SYMBOL_SUBST subst;

				mContext.cur++;

				subst.len = 2;
				subst.cur = mContext.cur + 1;
				mSubstStack.push(subst);
			}
			else
			{
				m_bCharIgnored = true;
				PutBack();
				mContext.ch = '\r';
			}
		}

		switch (mContext.ch)
		{
			case '\n':
				mContext.line++;
				mContext.col = 0;
				break;

			case '\t':
				mContext.tab_num++;

			default:
				mContext.col++;
		}

		mContext.cur++;
	}

	if (!ignore)
		mContext.value_buffer += mContext.ch;
	
	m_bCharIgnored = ignore;

	return mContext.ch;
}


bool CLexer::Match(const char* match)
{
	Mark();

	for (int i = 0; match[i] != 0; i++)
	{
		if (ReadChar(true) != match[i])
			return false;
	}

	Rollback();

	return true;	
}


void CLexer::PutBack()
{
	int len;

	if (mContext.eof)
	{
		mContext.eof = false;
		return;
	}

	if (!mSubstStack.empty() && mSubstStack.top().cur == mContext.cur)
	{
		len = mSubstStack.top().len;
		mSubstStack.pop();
	}
	else
	{
		if (mContext.target_cur > 0)
			len = (U16_IS_TRAIL(m_pTargetBuf[mContext.target_cur - 1])) ? 2 : 1;
		else
			len = 1;
	}

	if (mContext.target_cur - len < 0)
	{
		if (m_bReuseBuffer)
			throw new CTooDeepPutback();
		else
		{
			m_bReuseBuffer = true;
			m_nCurBufIndex = 1 - m_nCurBufIndex;
			m_pTargetBuf = m_chTargetBuf[m_nCurBufIndex]; 
			m_ppTargetBound = &(m_pTargetBound[m_nCurBufIndex]);
			mContext.target_cur = *m_ppTargetBound - m_pTargetBuf - len + mContext.target_cur;
			mContext.target_frame_id--;
		}
	}
	else
	{
		if (!mContext.eof)
		{
			mContext.target_cur -= len;
			mContext.cur -= len;
		}
	}

	mContext.eof = false;

	Eat();
}


bool CLexer::IsEOF()
{
	return mContext.eof;
}


void CLexer::Eat()
{
	int32_t len;

	switch (mContext.ch)
	{
		case '\n':
		case '\r':
			if (mContext.line > 0L)
				mContext.line--;
			mContext.col = 0;
			break;

		case '\t':
			mContext.tab_num--;
		
		default:
			mContext.col--;

	}

	len = mContext.value_buffer.length();

	if (len > 0 && !m_bCharIgnored)
	{
		mContext.value_buffer.remove(len - 1, 1);

		if (len > 1)
			mContext.ch = mContext.value_buffer[len - 2];
		else
			mContext.ch = 0;
	}
}


void CLexer::AppendCharToBuffer(UChar32 ch)
{
	if (ch == 0)
		mContext.value_buffer.append(ZERO_UCHAR);
	else
		mContext.value_buffer.append(ch);
}


UChar32 CLexer::GetNextChar()
{
	if (m_pTargetBuf + mContext.target_cur >= *m_ppTargetBound && m_bLastBlock && !mContext.buf_overflow && !m_bReuseBuffer)
	{
		mContext.eof = true;
		return 0;
	}

	if (mContext.target_cur >= 0 && m_pTargetBuf + mContext.target_cur < *m_ppTargetBound)
	{
		UChar32 ch;

		U16_NEXT_UNSAFE(m_pTargetBuf, mContext.target_cur, ch);
		return ch;
	}
	else
	{
		UErrorCode err = U_ZERO_ERROR;

		if (m_bReuseBuffer)
		{
			m_bReuseBuffer = false;
			m_nCurBufIndex = 1 - m_nCurBufIndex;
			m_pTargetBuf = m_chTargetBuf[m_nCurBufIndex];
			m_ppTargetBound = &(m_pTargetBound[m_nCurBufIndex]);
			mContext.target_cur = 1;
			mContext.target_frame_id++;

			return m_pTargetBuf[0];
		}
		else
		{
			if (mContext.target_cur < 0 || !mContext.buf_overflow)
			{
				ReadBlock();
				mContext.source_bound = m_chSourceBuf;
			}

			m_nCurBufIndex = 1 - m_nCurBufIndex;
			m_pTargetBuf = m_chTargetBuf[m_nCurBufIndex];
			m_ppTargetBound = &(m_pTargetBound[m_nCurBufIndex]);
			*m_ppTargetBound = m_pTargetBuf;
			mContext.target_cur = 0;
			mContext.prev_bound = mContext.source_bound;
			mContext.target_frame_id++;

			if (mContext.source_len == 0)
				return 0;

			ucnv_toUnicode(m_pConverter, m_ppTargetBound, m_pTargetBuf + CONV_BUF_SIZE/ucnv_getMinCharSize(m_pConverter),
				(const char**)&mContext.source_bound, m_chSourceBuf + mContext.source_len, NULL, m_bLastBlock, &err);

			if (err == U_BUFFER_OVERFLOW_ERROR)
				mContext.buf_overflow = true;
			else
			{
				if (U_FAILURE(err))
					throw new CInputConversionErrorException();
				else
					mContext.buf_overflow = false;
			}

			if (mContext.target_cur == 0 && m_pTargetBuf[0] == 0xFEFF)
					mContext.target_cur = 1;

			return m_pTargetBuf[mContext.target_cur++];
		}
	}
} 


void CLexer::ReadBlock()
{
	m_pStream->read(m_chSourceBuf, CONV_BUF_SIZE);

	if (m_pStream->fail() && !m_pStream->eof())
		throw new CInputStreamReadErrorException();

	mContext.source_len = m_pStream->gcount();
	m_bLastBlock = m_pStream->eof();
	m_pStream->clear();
}


CLexer::~CLexer()
{
	if (m_pConverter)
	{
		ucnv_flushCache();
		ucnv_close(m_pConverter);
	}
}
