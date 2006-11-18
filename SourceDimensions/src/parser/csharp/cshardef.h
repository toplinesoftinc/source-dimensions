#ifndef cshardef_INCLUDED
#define cshardef_INCLUDED

enum {
      ERROR_CODE,
      BEFORE_CODE,
      INSERTION_CODE,
      INVALID_CODE,
      SUBSTITUTION_CODE,
      DELETION_CODE,
      MERGE_CODE,
      MISPLACED_CODE,
      SCOPE_CODE,
      MANUAL_CODE,
      SECONDARY_CODE,
      EOF_CODE,

      ERROR_SYMBOL      = 144,
      MAX_DISTANCE      = 30,
      MIN_DISTANCE      = 3,
      MAX_NAME_LENGTH   = 21,
      MAX_TERM_LENGTH   = 21,
      NUM_STATES        = 1517,

      NT_OFFSET         = 144,
      BUFF_UBOUND       = 32796,
      BUFF_SIZE         = 32797,
      STACK_UBOUND      = 127,
      STACK_SIZE        = 128,
      SCOPE_UBOUND      = -1,
      SCOPE_SIZE        = 0,
      LA_STATE_OFFSET   = 3393,
      MAX_LA            = 32767,
      NUM_RULES         = 791,
      NUM_TERMINALS     = 144,
      NUM_NON_TERMINALS = 334,
      NUM_SYMBOLS       = 478,
      START_STATE       = 1362,
      EOFT_SYMBOL       = 143,
      EOLT_SYMBOL       = 143,
      ACCEPT_ACTION     = 3392,
      ERROR_ACTION      = 3393
     };


#endif /* cshardef_INCLUDED */
