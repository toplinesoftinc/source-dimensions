#ifndef csharprs_INCLUDED
#define csharprs_INCLUDED

#undef  SCOPE_REPAIR
#define DEFERRED_RECOVERY
#define FULL_DIAGNOSIS
#define SPACE_TABLES

class CLexer;

class csharprs_table
{
public:
    static int original_state(int state) { return -base_check[state]; }
    static int asi(int state) { return asb[original_state(state)]; }
    static int nasi(int state) { return nasb[original_state(state)]; }

    static const unsigned char  rhs[];
    static const   signed short check_table[];
    static const   signed short *base_check;
    static const unsigned short lhs[];
    static const unsigned short *base_action;
    static const unsigned short default_goto[];
    static const unsigned char  term_check[];
    static const unsigned short term_action[];

    static const unsigned short asb[];
    static const unsigned char  asr[];
    static const unsigned short nasb[];
    static const unsigned short nasr[];
    static const unsigned short name_start[];
    static const unsigned char  name_length[];
    static const          char  string_buffer[];
    static const unsigned short terminal_index[];
    static const unsigned short non_terminal_index[];

    static int nt_action(int state, int sym)
    {
        return (base_check[state + sym] == sym)
                             ? base_action[state + sym]
                             : default_goto[sym];
    }

    static int t_action(int act, int sym, CLexer *lexer)
    {
        act = base_action[act];
        int i = act + sym;

        act = term_action[term_check[i] == sym ? i : act];

        if (act > LA_STATE_OFFSET)
        {
            lexer->Mark();
            for (sym = lexer->GetNextToken();;sym = lexer->GetNextToken())
            {
               act -= LA_STATE_OFFSET;
               i = act + sym;
               act = term_action[term_check[i] == sym ? i : act];
               if (act <= LA_STATE_OFFSET)
                   break;
            } 
            lexer->Rollback();
        }

        return act;
    }
};

#endif /* csharprs_INCLUDED */
