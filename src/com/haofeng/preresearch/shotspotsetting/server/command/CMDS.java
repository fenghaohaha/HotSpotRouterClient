package com.haofeng.preresearch.shotspotsetting.server.command;

/**
 * 交互命令
 * @author fenghao
 *
 */
public class CMDS {
	
	/** 客户端申请认证 */
	public final static String APPLY_FOR_AUTHEN = "apply_for_authen";
	/** 客户端申请通话 */
	public final static String APPLY_FOR_CALL = "apply_for_call";
	/** 服务端返回给客户端状态,认证失败 */
	public final static String AUTHEN_FAILED = "authen_failed";
	/** 认证成功 */
	public final static String AUTHEN_SUCCESS = "authen_success";
	/** 无法申请到通话资源,多种状态 */
	public final static String CALL_FAILED = "call_failed";
	/** 允许客户端拨打 */
	public final static String CALL_SUCCESS = "call_success";

}
