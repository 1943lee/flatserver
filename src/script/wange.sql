-- 模拟万格环境进行测试用到的SQL
-- 创建 PIPE 表
CREATE TABLE t_pipe
  (
    ID      INT NOT NULL,
    CALL_ID VARCHAR2(64),
    STATUS  NUMBER(1, 0),
    SENDER  NUMBER(1, 0),
    SEAT    CHAR(3),
    SEAT_IP VARCHAR2(50),
    P_ID    NUMBER(5, 0),
    TIME    DATE,
    CONTENT VARCHAR2(1024),
    CONSTRAINT T_PIPE_PK PRIMARY KEY(ID) ENABLE
  );
  -- 创建 MSG 表
  CREATE TABLE t_msg
  (
    ID      INT NOT NULL,
    CALL_ID VARCHAR2(64),
    STATUS  NUMBER(1, 0),
    SENDER  NUMBER(1, 0),
    SEAT    CHAR(3),
    SEAT_IP VARCHAR2(50),
    P_ID    NUMBER(5, 0),
    TIME    DATE,
    CONTENT VARCHAR2(1024),
    CONSTRAINT T_MSG_PK PRIMARY KEY(ID) ENABLE
  );
  -- PDT 方应答序号
  CREATE SEQUENCE "SEQ_EVEN" MINVALUE 0 MAXVALUE 999999999999999999 INCREMENT BY 2 START WITH 0 CACHE 20 ORDER CYCLE ;
  -- EZVIEW 方请求序号
  CREATE SEQUENCE "SEQ_ODD"  MINVALUE 1 MAXVALUE 999999999999999999 INCREMENT BY 2 START WITH 1 CACHE 20 ORDER CYCLE ;
SELECT * FROM t_pipe;
SELECT * FROM t_msg;
-- 手动插入组会成功应答
INSERT
INTO t_msg
  (SELECT seq_even.nextval                                                                                                                                           AS id,
      p.call_id                                                                                                                                                      AS call_id,
      2                                                                                                                                                              AS status,
      2                                                                                                                                                              AS sender,
      NULL                                                                                                                                                           AS seat,
      NULL                                                                                                                                                           AS seat_ip,
      4                                                                                                                                                              AS p_id,
      sysdate                                                                                                                                                        AS TIME,
      REPLACE( REPLACE(p.content, '<to></to>', '<to>3471</to>'), '<call-parameter>0</call-parameter>', '<call-parameter>0</call-parameter><response>200</response>') AS content
    FROM t_pipe p
  );

  DELETE FROM t_msg;

-- 创建万格组号使用情况表
CREATE TABLE t_GROUP_no
  (
    WANGE_GROUP_NO VARCHAR(128),
    CVS_MEETING_ID VARCHAR(256),
    wange_from_devices varchar2(4000),
    CONSTRAINT T_group_no_PK PRIMARY KEY(wange_group_no) ENABLE
  );

-- 插入组号配置
insert into t_group_no values('44021941', null, null);
insert into t_group_no values('44021942', null, null);
insert into t_group_no values('44021943', null, null);
insert into t_group_no values('44021944', null, null);
insert into t_group_no values('44021945', null, null);
insert into t_group_no values('44021946', null, null);
insert into t_group_no values('44021947', null, null);
insert into t_group_no values('44021948', null, null);
insert into t_group_no values('44021949', null, null);