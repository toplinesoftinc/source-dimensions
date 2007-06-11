#ifndef CSha2def_INCLUDED
#define CSha2def_INCLUDED

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

      ERROR_SYMBOL      = 148,
      MAX_DISTANCE      = 30,
      MIN_DISTANCE      = 3,
      MAX_NAME_LENGTH   = 21,
      MAX_TERM_LENGTH   = 21,
      NUM_STATES        = 1918,

      NT_OFFSET         = 148,
      BUFF_UBOUND       = 32,
      BUFF_SIZE         = 33,
      STACK_UBOUND      = 127,
      STACK_SIZE        = 128,
      SCOPE_UBOUND      = -1,
      SCOPE_SIZE        = 0,
      LA_STATE_OFFSET   = 4345,
      MAX_LA            = 3,
      NUM_RULES         = 1083,
      NUM_TERMINALS     = 148,
      NUM_NON_TERMINALS = 426,
      NUM_SYMBOLS       = 574,
      START_STATE       = 1489,
      EOFT_SYMBOL       = 147,
      EOLT_SYMBOL       = 147,
      ACCEPT_ACTION     = 4344,
      ERROR_ACTION      = 4345
     };


#endif /* CSha2def_INCLUDED */
