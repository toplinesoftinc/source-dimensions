#ifndef Javadef_INCLUDED
#define Javadef_INCLUDED

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

      ERROR_SYMBOL      = 103,
      MAX_DISTANCE      = 30,
      MIN_DISTANCE      = 3,
      MAX_NAME_LENGTH   = 21,
      MAX_TERM_LENGTH   = 21,
      NUM_STATES        = 682,

      NT_OFFSET         = 103,
      BUFF_UBOUND       = 31,
      BUFF_SIZE         = 32,
      STACK_UBOUND      = 127,
      STACK_SIZE        = 128,
      SCOPE_UBOUND      = -1,
      SCOPE_SIZE        = 0,
      LA_STATE_OFFSET   = 1718,
      MAX_LA            = 2,
      NUM_RULES         = 417,
      NUM_TERMINALS     = 103,
      NUM_NON_TERMINALS = 170,
      NUM_SYMBOLS       = 273,
      START_STATE       = 862,
      EOFT_SYMBOL       = 102,
      EOLT_SYMBOL       = 102,
      ACCEPT_ACTION     = 1717,
      ERROR_ACTION      = 1718
     };


#endif /* Javadef_INCLUDED */
