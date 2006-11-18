#ifndef Java5def_INCLUDED
#define Java5def_INCLUDED

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

      ERROR_SYMBOL      = 102,
      MAX_DISTANCE      = 30,
      MIN_DISTANCE      = 3,
      MAX_NAME_LENGTH   = 21,
      MAX_TERM_LENGTH   = 21,
      NUM_STATES        = 1022,

      NT_OFFSET         = 102,
      BUFF_UBOUND       = 33,
      BUFF_SIZE         = 34,
      STACK_UBOUND      = 127,
      STACK_SIZE        = 128,
      SCOPE_UBOUND      = -1,
      SCOPE_SIZE        = 0,
      LA_STATE_OFFSET   = 2500,
      MAX_LA            = 4,
      NUM_RULES         = 579,
      NUM_TERMINALS     = 102,
      NUM_NON_TERMINALS = 211,
      NUM_SYMBOLS       = 313,
      START_STATE       = 1154,
      EOFT_SYMBOL       = 101,
      EOLT_SYMBOL       = 101,
      ACCEPT_ACTION     = 2499,
      ERROR_ACTION      = 2500
     };


#endif /* Java5def_INCLUDED */
