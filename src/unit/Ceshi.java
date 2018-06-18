package unit;

public class Ceshi
{
  private static boolean ifCeiShi = false;
  
  /* Error */
  public static void ceshi_write_1(String infos)
  {
    // Byte code:
    //   0: getstatic 10	unit/Ceshi:ifCeiShi	Z
    //   3: ifeq +117 -> 120
    //   6: aconst_null
    //   7: astore_1
    //   8: new 21	java/io/OutputStreamWriter
    //   11: dup
    //   12: new 23	java/io/FileOutputStream
    //   15: dup
    //   16: new 25	java/io/File
    //   19: dup
    //   20: ldc 27
    //   22: invokespecial 29	java/io/File:<init>	(Ljava/lang/String;)V
    //   25: invokespecial 31	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   28: ldc 34
    //   30: invokespecial 36	java/io/OutputStreamWriter:<init>	(Ljava/io/OutputStream;Ljava/lang/String;)V
    //   33: astore_2
    //   34: new 39	java/io/BufferedWriter
    //   37: dup
    //   38: aload_2
    //   39: invokespecial 41	java/io/BufferedWriter:<init>	(Ljava/io/Writer;)V
    //   42: astore_1
    //   43: aload_1
    //   44: aload_0
    //   45: invokevirtual 44	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   48: aload_1
    //   49: invokevirtual 47	java/io/BufferedWriter:flush	()V
    //   52: goto +50 -> 102
    //   55: astore_2
    //   56: aload_2
    //   57: invokevirtual 50	java/io/IOException:printStackTrace	()V
    //   60: aload_1
    //   61: ifnull +59 -> 120
    //   64: aload_1
    //   65: invokevirtual 55	java/io/BufferedWriter:close	()V
    //   68: goto +52 -> 120
    //   71: astore 4
    //   73: aload 4
    //   75: invokevirtual 50	java/io/IOException:printStackTrace	()V
    //   78: goto +42 -> 120
    //   81: astore_3
    //   82: aload_1
    //   83: ifnull +17 -> 100
    //   86: aload_1
    //   87: invokevirtual 55	java/io/BufferedWriter:close	()V
    //   90: goto +10 -> 100
    //   93: astore 4
    //   95: aload 4
    //   97: invokevirtual 50	java/io/IOException:printStackTrace	()V
    //   100: aload_3
    //   101: athrow
    //   102: aload_1
    //   103: ifnull +17 -> 120
    //   106: aload_1
    //   107: invokevirtual 55	java/io/BufferedWriter:close	()V
    //   110: goto +10 -> 120
    //   113: astore 4
    //   115: aload 4
    //   117: invokevirtual 50	java/io/IOException:printStackTrace	()V
    //   120: return
    // Line number table:
    //   Java source line #22	-> byte code offset #0
    //   Java source line #23	-> byte code offset #6
    //   Java source line #26	-> byte code offset #8
    //   Java source line #27	-> byte code offset #34
    //   Java source line #28	-> byte code offset #43
    //   Java source line #29	-> byte code offset #48
    //   Java source line #30	-> byte code offset #52
    //   Java source line #31	-> byte code offset #56
    //   Java source line #33	-> byte code offset #60
    //   Java source line #35	-> byte code offset #64
    //   Java source line #36	-> byte code offset #68
    //   Java source line #37	-> byte code offset #73
    //   Java source line #32	-> byte code offset #81
    //   Java source line #33	-> byte code offset #82
    //   Java source line #35	-> byte code offset #86
    //   Java source line #36	-> byte code offset #90
    //   Java source line #37	-> byte code offset #95
    //   Java source line #41	-> byte code offset #100
    //   Java source line #33	-> byte code offset #102
    //   Java source line #35	-> byte code offset #106
    //   Java source line #36	-> byte code offset #110
    //   Java source line #37	-> byte code offset #115
    //   Java source line #44	-> byte code offset #120
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	121	0	infos	String
    //   7	100	1	pw	java.io.BufferedWriter
    //   33	6	2	osw	java.io.OutputStreamWriter
    //   55	2	2	var11	java.io.IOException
    //   81	20	3	localObject	Object
    //   71	3	4	var10	java.io.IOException
    //   93	3	4	var10	java.io.IOException
    //   113	3	4	var10	java.io.IOException
    // Exception table:
    //   from	to	target	type
    //   8	52	55	java/io/IOException
    //   64	68	71	java/io/IOException
    //   8	60	81	finally
    //   86	90	93	java/io/IOException
    //   106	110	113	java/io/IOException
  }
  
  /* Error */
  public static void ceshi_write_2(String infos)
  {
    // Byte code:
    //   0: getstatic 10	unit/Ceshi:ifCeiShi	Z
    //   3: ifeq +117 -> 120
    //   6: aconst_null
    //   7: astore_1
    //   8: new 21	java/io/OutputStreamWriter
    //   11: dup
    //   12: new 23	java/io/FileOutputStream
    //   15: dup
    //   16: new 25	java/io/File
    //   19: dup
    //   20: ldc 73
    //   22: invokespecial 29	java/io/File:<init>	(Ljava/lang/String;)V
    //   25: invokespecial 31	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   28: ldc 34
    //   30: invokespecial 36	java/io/OutputStreamWriter:<init>	(Ljava/io/OutputStream;Ljava/lang/String;)V
    //   33: astore_2
    //   34: new 39	java/io/BufferedWriter
    //   37: dup
    //   38: aload_2
    //   39: invokespecial 41	java/io/BufferedWriter:<init>	(Ljava/io/Writer;)V
    //   42: astore_1
    //   43: aload_1
    //   44: aload_0
    //   45: invokevirtual 44	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   48: aload_1
    //   49: invokevirtual 47	java/io/BufferedWriter:flush	()V
    //   52: goto +50 -> 102
    //   55: astore_2
    //   56: aload_2
    //   57: invokevirtual 50	java/io/IOException:printStackTrace	()V
    //   60: aload_1
    //   61: ifnull +59 -> 120
    //   64: aload_1
    //   65: invokevirtual 55	java/io/BufferedWriter:close	()V
    //   68: goto +52 -> 120
    //   71: astore 4
    //   73: aload 4
    //   75: invokevirtual 50	java/io/IOException:printStackTrace	()V
    //   78: goto +42 -> 120
    //   81: astore_3
    //   82: aload_1
    //   83: ifnull +17 -> 100
    //   86: aload_1
    //   87: invokevirtual 55	java/io/BufferedWriter:close	()V
    //   90: goto +10 -> 100
    //   93: astore 4
    //   95: aload 4
    //   97: invokevirtual 50	java/io/IOException:printStackTrace	()V
    //   100: aload_3
    //   101: athrow
    //   102: aload_1
    //   103: ifnull +17 -> 120
    //   106: aload_1
    //   107: invokevirtual 55	java/io/BufferedWriter:close	()V
    //   110: goto +10 -> 120
    //   113: astore 4
    //   115: aload 4
    //   117: invokevirtual 50	java/io/IOException:printStackTrace	()V
    //   120: return
    // Line number table:
    //   Java source line #47	-> byte code offset #0
    //   Java source line #48	-> byte code offset #6
    //   Java source line #51	-> byte code offset #8
    //   Java source line #52	-> byte code offset #34
    //   Java source line #53	-> byte code offset #43
    //   Java source line #54	-> byte code offset #48
    //   Java source line #55	-> byte code offset #52
    //   Java source line #56	-> byte code offset #56
    //   Java source line #58	-> byte code offset #60
    //   Java source line #60	-> byte code offset #64
    //   Java source line #61	-> byte code offset #68
    //   Java source line #62	-> byte code offset #73
    //   Java source line #57	-> byte code offset #81
    //   Java source line #58	-> byte code offset #82
    //   Java source line #60	-> byte code offset #86
    //   Java source line #61	-> byte code offset #90
    //   Java source line #62	-> byte code offset #95
    //   Java source line #66	-> byte code offset #100
    //   Java source line #58	-> byte code offset #102
    //   Java source line #60	-> byte code offset #106
    //   Java source line #61	-> byte code offset #110
    //   Java source line #62	-> byte code offset #115
    //   Java source line #69	-> byte code offset #120
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	121	0	infos	String
    //   7	100	1	pw	java.io.BufferedWriter
    //   33	6	2	osw	java.io.OutputStreamWriter
    //   55	2	2	var11	java.io.IOException
    //   81	20	3	localObject	Object
    //   71	3	4	var10	java.io.IOException
    //   93	3	4	var10	java.io.IOException
    //   113	3	4	var10	java.io.IOException
    // Exception table:
    //   from	to	target	type
    //   8	52	55	java/io/IOException
    //   64	68	71	java/io/IOException
    //   8	60	81	finally
    //   86	90	93	java/io/IOException
    //   106	110	113	java/io/IOException
  }
  
  /* Error */
  public static void ceshi_write_3(String infos)
  {
    // Byte code:
    //   0: getstatic 10	unit/Ceshi:ifCeiShi	Z
    //   3: ifeq +117 -> 120
    //   6: aconst_null
    //   7: astore_1
    //   8: new 21	java/io/OutputStreamWriter
    //   11: dup
    //   12: new 23	java/io/FileOutputStream
    //   15: dup
    //   16: new 25	java/io/File
    //   19: dup
    //   20: ldc 76
    //   22: invokespecial 29	java/io/File:<init>	(Ljava/lang/String;)V
    //   25: invokespecial 31	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   28: ldc 34
    //   30: invokespecial 36	java/io/OutputStreamWriter:<init>	(Ljava/io/OutputStream;Ljava/lang/String;)V
    //   33: astore_2
    //   34: new 39	java/io/BufferedWriter
    //   37: dup
    //   38: aload_2
    //   39: invokespecial 41	java/io/BufferedWriter:<init>	(Ljava/io/Writer;)V
    //   42: astore_1
    //   43: aload_1
    //   44: aload_0
    //   45: invokevirtual 44	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   48: aload_1
    //   49: invokevirtual 47	java/io/BufferedWriter:flush	()V
    //   52: goto +50 -> 102
    //   55: astore_2
    //   56: aload_2
    //   57: invokevirtual 50	java/io/IOException:printStackTrace	()V
    //   60: aload_1
    //   61: ifnull +59 -> 120
    //   64: aload_1
    //   65: invokevirtual 55	java/io/BufferedWriter:close	()V
    //   68: goto +52 -> 120
    //   71: astore 4
    //   73: aload 4
    //   75: invokevirtual 50	java/io/IOException:printStackTrace	()V
    //   78: goto +42 -> 120
    //   81: astore_3
    //   82: aload_1
    //   83: ifnull +17 -> 100
    //   86: aload_1
    //   87: invokevirtual 55	java/io/BufferedWriter:close	()V
    //   90: goto +10 -> 100
    //   93: astore 4
    //   95: aload 4
    //   97: invokevirtual 50	java/io/IOException:printStackTrace	()V
    //   100: aload_3
    //   101: athrow
    //   102: aload_1
    //   103: ifnull +17 -> 120
    //   106: aload_1
    //   107: invokevirtual 55	java/io/BufferedWriter:close	()V
    //   110: goto +10 -> 120
    //   113: astore 4
    //   115: aload 4
    //   117: invokevirtual 50	java/io/IOException:printStackTrace	()V
    //   120: return
    // Line number table:
    //   Java source line #72	-> byte code offset #0
    //   Java source line #73	-> byte code offset #6
    //   Java source line #76	-> byte code offset #8
    //   Java source line #77	-> byte code offset #34
    //   Java source line #78	-> byte code offset #43
    //   Java source line #79	-> byte code offset #48
    //   Java source line #80	-> byte code offset #52
    //   Java source line #81	-> byte code offset #56
    //   Java source line #83	-> byte code offset #60
    //   Java source line #85	-> byte code offset #64
    //   Java source line #86	-> byte code offset #68
    //   Java source line #87	-> byte code offset #73
    //   Java source line #82	-> byte code offset #81
    //   Java source line #83	-> byte code offset #82
    //   Java source line #85	-> byte code offset #86
    //   Java source line #86	-> byte code offset #90
    //   Java source line #87	-> byte code offset #95
    //   Java source line #91	-> byte code offset #100
    //   Java source line #83	-> byte code offset #102
    //   Java source line #85	-> byte code offset #106
    //   Java source line #86	-> byte code offset #110
    //   Java source line #87	-> byte code offset #115
    //   Java source line #94	-> byte code offset #120
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	121	0	infos	String
    //   7	100	1	pw	java.io.BufferedWriter
    //   33	6	2	osw	java.io.OutputStreamWriter
    //   55	2	2	var11	java.io.IOException
    //   81	20	3	localObject	Object
    //   71	3	4	var10	java.io.IOException
    //   93	3	4	var10	java.io.IOException
    //   113	3	4	var10	java.io.IOException
    // Exception table:
    //   from	to	target	type
    //   8	52	55	java/io/IOException
    //   64	68	71	java/io/IOException
    //   8	60	81	finally
    //   86	90	93	java/io/IOException
    //   106	110	113	java/io/IOException
  }
  
  /* Error */
  public static void ceshi_write_N(String infos, int n)
  {
    // Byte code:
    //   0: getstatic 10	unit/Ceshi:ifCeiShi	Z
    //   3: ifeq +138 -> 141
    //   6: aconst_null
    //   7: astore_2
    //   8: new 21	java/io/OutputStreamWriter
    //   11: dup
    //   12: new 23	java/io/FileOutputStream
    //   15: dup
    //   16: new 25	java/io/File
    //   19: dup
    //   20: new 80	java/lang/StringBuilder
    //   23: dup
    //   24: ldc 82
    //   26: invokespecial 84	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   29: iload_1
    //   30: invokevirtual 85	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   33: ldc 89
    //   35: invokevirtual 91	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   38: invokevirtual 94	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   41: invokespecial 29	java/io/File:<init>	(Ljava/lang/String;)V
    //   44: invokespecial 31	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   47: ldc 34
    //   49: invokespecial 36	java/io/OutputStreamWriter:<init>	(Ljava/io/OutputStream;Ljava/lang/String;)V
    //   52: astore_3
    //   53: new 39	java/io/BufferedWriter
    //   56: dup
    //   57: aload_3
    //   58: invokespecial 41	java/io/BufferedWriter:<init>	(Ljava/io/Writer;)V
    //   61: astore_2
    //   62: aload_2
    //   63: aload_0
    //   64: invokevirtual 44	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   67: aload_2
    //   68: invokevirtual 47	java/io/BufferedWriter:flush	()V
    //   71: goto +52 -> 123
    //   74: astore_3
    //   75: aload_3
    //   76: invokevirtual 50	java/io/IOException:printStackTrace	()V
    //   79: aload_2
    //   80: ifnull +61 -> 141
    //   83: aload_2
    //   84: invokevirtual 55	java/io/BufferedWriter:close	()V
    //   87: goto +54 -> 141
    //   90: astore 5
    //   92: aload 5
    //   94: invokevirtual 50	java/io/IOException:printStackTrace	()V
    //   97: goto +44 -> 141
    //   100: astore 4
    //   102: aload_2
    //   103: ifnull +17 -> 120
    //   106: aload_2
    //   107: invokevirtual 55	java/io/BufferedWriter:close	()V
    //   110: goto +10 -> 120
    //   113: astore 5
    //   115: aload 5
    //   117: invokevirtual 50	java/io/IOException:printStackTrace	()V
    //   120: aload 4
    //   122: athrow
    //   123: aload_2
    //   124: ifnull +17 -> 141
    //   127: aload_2
    //   128: invokevirtual 55	java/io/BufferedWriter:close	()V
    //   131: goto +10 -> 141
    //   134: astore 5
    //   136: aload 5
    //   138: invokevirtual 50	java/io/IOException:printStackTrace	()V
    //   141: return
    // Line number table:
    //   Java source line #97	-> byte code offset #0
    //   Java source line #98	-> byte code offset #6
    //   Java source line #101	-> byte code offset #8
    //   Java source line #102	-> byte code offset #53
    //   Java source line #103	-> byte code offset #62
    //   Java source line #104	-> byte code offset #67
    //   Java source line #105	-> byte code offset #71
    //   Java source line #106	-> byte code offset #75
    //   Java source line #108	-> byte code offset #79
    //   Java source line #110	-> byte code offset #83
    //   Java source line #111	-> byte code offset #87
    //   Java source line #112	-> byte code offset #92
    //   Java source line #107	-> byte code offset #100
    //   Java source line #108	-> byte code offset #102
    //   Java source line #110	-> byte code offset #106
    //   Java source line #111	-> byte code offset #110
    //   Java source line #112	-> byte code offset #115
    //   Java source line #116	-> byte code offset #120
    //   Java source line #108	-> byte code offset #123
    //   Java source line #110	-> byte code offset #127
    //   Java source line #111	-> byte code offset #131
    //   Java source line #112	-> byte code offset #136
    //   Java source line #119	-> byte code offset #141
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	142	0	infos	String
    //   0	142	1	n	int
    //   7	121	2	pw	java.io.BufferedWriter
    //   52	6	3	osw	java.io.OutputStreamWriter
    //   74	2	3	var12	java.io.IOException
    //   100	21	4	localObject	Object
    //   90	3	5	var11	java.io.IOException
    //   113	3	5	var11	java.io.IOException
    //   134	3	5	var11	java.io.IOException
    // Exception table:
    //   from	to	target	type
    //   8	71	74	java/io/IOException
    //   83	87	90	java/io/IOException
    //   8	79	100	finally
    //   106	110	113	java/io/IOException
    //   127	131	134	java/io/IOException
  }
  
  /* Error */
  public static void ceshiZhuiJia1(String infos)
  {
    // Byte code:
    //   0: getstatic 10	unit/Ceshi:ifCeiShi	Z
    //   3: ifeq +118 -> 121
    //   6: aconst_null
    //   7: astore_1
    //   8: new 21	java/io/OutputStreamWriter
    //   11: dup
    //   12: new 23	java/io/FileOutputStream
    //   15: dup
    //   16: new 25	java/io/File
    //   19: dup
    //   20: ldc 102
    //   22: invokespecial 29	java/io/File:<init>	(Ljava/lang/String;)V
    //   25: invokespecial 31	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   28: ldc 34
    //   30: invokespecial 36	java/io/OutputStreamWriter:<init>	(Ljava/io/OutputStream;Ljava/lang/String;)V
    //   33: astore_2
    //   34: new 39	java/io/BufferedWriter
    //   37: dup
    //   38: aload_2
    //   39: invokespecial 41	java/io/BufferedWriter:<init>	(Ljava/io/Writer;)V
    //   42: astore_1
    //   43: aload_1
    //   44: aload_0
    //   45: invokevirtual 104	java/io/BufferedWriter:append	(Ljava/lang/CharSequence;)Ljava/io/Writer;
    //   48: pop
    //   49: aload_1
    //   50: invokevirtual 47	java/io/BufferedWriter:flush	()V
    //   53: goto +50 -> 103
    //   56: astore_2
    //   57: aload_2
    //   58: invokevirtual 50	java/io/IOException:printStackTrace	()V
    //   61: aload_1
    //   62: ifnull +59 -> 121
    //   65: aload_1
    //   66: invokevirtual 55	java/io/BufferedWriter:close	()V
    //   69: goto +52 -> 121
    //   72: astore 4
    //   74: aload 4
    //   76: invokevirtual 50	java/io/IOException:printStackTrace	()V
    //   79: goto +42 -> 121
    //   82: astore_3
    //   83: aload_1
    //   84: ifnull +17 -> 101
    //   87: aload_1
    //   88: invokevirtual 55	java/io/BufferedWriter:close	()V
    //   91: goto +10 -> 101
    //   94: astore 4
    //   96: aload 4
    //   98: invokevirtual 50	java/io/IOException:printStackTrace	()V
    //   101: aload_3
    //   102: athrow
    //   103: aload_1
    //   104: ifnull +17 -> 121
    //   107: aload_1
    //   108: invokevirtual 55	java/io/BufferedWriter:close	()V
    //   111: goto +10 -> 121
    //   114: astore 4
    //   116: aload 4
    //   118: invokevirtual 50	java/io/IOException:printStackTrace	()V
    //   121: return
    // Line number table:
    //   Java source line #122	-> byte code offset #0
    //   Java source line #123	-> byte code offset #6
    //   Java source line #126	-> byte code offset #8
    //   Java source line #127	-> byte code offset #34
    //   Java source line #128	-> byte code offset #43
    //   Java source line #129	-> byte code offset #49
    //   Java source line #130	-> byte code offset #53
    //   Java source line #131	-> byte code offset #57
    //   Java source line #133	-> byte code offset #61
    //   Java source line #135	-> byte code offset #65
    //   Java source line #136	-> byte code offset #69
    //   Java source line #137	-> byte code offset #74
    //   Java source line #132	-> byte code offset #82
    //   Java source line #133	-> byte code offset #83
    //   Java source line #135	-> byte code offset #87
    //   Java source line #136	-> byte code offset #91
    //   Java source line #137	-> byte code offset #96
    //   Java source line #141	-> byte code offset #101
    //   Java source line #133	-> byte code offset #103
    //   Java source line #135	-> byte code offset #107
    //   Java source line #136	-> byte code offset #111
    //   Java source line #137	-> byte code offset #116
    //   Java source line #144	-> byte code offset #121
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	122	0	infos	String
    //   7	101	1	pw	java.io.BufferedWriter
    //   33	6	2	osw	java.io.OutputStreamWriter
    //   56	2	2	var11	java.io.IOException
    //   82	20	3	localObject	Object
    //   72	3	4	var10	java.io.IOException
    //   94	3	4	var10	java.io.IOException
    //   114	3	4	var10	java.io.IOException
    // Exception table:
    //   from	to	target	type
    //   8	53	56	java/io/IOException
    //   65	69	72	java/io/IOException
    //   8	61	82	finally
    //   87	91	94	java/io/IOException
    //   107	111	114	java/io/IOException
  }
  
  /* Error */
  public static void ceshiZhuiJia2(String infos)
  {
    // Byte code:
    //   0: getstatic 10	unit/Ceshi:ifCeiShi	Z
    //   3: ifeq +89 -> 92
    //   6: aconst_null
    //   7: astore_1
    //   8: new 108	java/io/FileWriter
    //   11: dup
    //   12: ldc 110
    //   14: iconst_1
    //   15: invokespecial 112	java/io/FileWriter:<init>	(Ljava/lang/String;Z)V
    //   18: astore_1
    //   19: aload_1
    //   20: aload_0
    //   21: invokevirtual 115	java/io/FileWriter:write	(Ljava/lang/String;)V
    //   24: goto +50 -> 74
    //   27: astore_2
    //   28: aload_2
    //   29: invokevirtual 50	java/io/IOException:printStackTrace	()V
    //   32: aload_1
    //   33: ifnull +59 -> 92
    //   36: aload_1
    //   37: invokevirtual 116	java/io/FileWriter:close	()V
    //   40: goto +52 -> 92
    //   43: astore 4
    //   45: aload 4
    //   47: invokevirtual 50	java/io/IOException:printStackTrace	()V
    //   50: goto +42 -> 92
    //   53: astore_3
    //   54: aload_1
    //   55: ifnull +17 -> 72
    //   58: aload_1
    //   59: invokevirtual 116	java/io/FileWriter:close	()V
    //   62: goto +10 -> 72
    //   65: astore 4
    //   67: aload 4
    //   69: invokevirtual 50	java/io/IOException:printStackTrace	()V
    //   72: aload_3
    //   73: athrow
    //   74: aload_1
    //   75: ifnull +17 -> 92
    //   78: aload_1
    //   79: invokevirtual 116	java/io/FileWriter:close	()V
    //   82: goto +10 -> 92
    //   85: astore 4
    //   87: aload 4
    //   89: invokevirtual 50	java/io/IOException:printStackTrace	()V
    //   92: return
    // Line number table:
    //   Java source line #147	-> byte code offset #0
    //   Java source line #148	-> byte code offset #6
    //   Java source line #151	-> byte code offset #8
    //   Java source line #152	-> byte code offset #19
    //   Java source line #153	-> byte code offset #24
    //   Java source line #154	-> byte code offset #28
    //   Java source line #157	-> byte code offset #32
    //   Java source line #158	-> byte code offset #36
    //   Java source line #160	-> byte code offset #40
    //   Java source line #161	-> byte code offset #45
    //   Java source line #155	-> byte code offset #53
    //   Java source line #157	-> byte code offset #54
    //   Java source line #158	-> byte code offset #58
    //   Java source line #160	-> byte code offset #62
    //   Java source line #161	-> byte code offset #67
    //   Java source line #164	-> byte code offset #72
    //   Java source line #157	-> byte code offset #74
    //   Java source line #158	-> byte code offset #78
    //   Java source line #160	-> byte code offset #82
    //   Java source line #161	-> byte code offset #87
    //   Java source line #167	-> byte code offset #92
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	93	0	infos	String
    //   7	72	1	writer	java.io.FileWriter
    //   27	2	2	var11	java.io.IOException
    //   53	20	3	localObject	Object
    //   43	3	4	var10	java.io.IOException
    //   65	3	4	var10	java.io.IOException
    //   85	3	4	var10	java.io.IOException
    // Exception table:
    //   from	to	target	type
    //   8	24	27	java/io/IOException
    //   32	40	43	java/io/IOException
    //   8	32	53	finally
    //   54	62	65	java/io/IOException
    //   74	82	85	java/io/IOException
  }
  
  public static void method2(String fileName, String content) {}
}
