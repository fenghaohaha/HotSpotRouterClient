package com.haofeng.preresearch.shotspotsetting.server.command;

/**
 * 命令基类
 * @author haofeng
 * @since Jue.30 2017
 */
public class BaseCommand {
	
	/** 命令 {@link CMDS}*/
	public String cmd;
	/** 发送方ip */
	public String ip;
	/** 发送方Mac地址 */
	public String macAddr;
	/** 认证信息 */
	public String authenMsg;
	/** 执行状态信息 */
	public String stateMsg;
	
	public String getStateMsg() {
		return stateMsg;
	}

	public void setStateMsg(String stateMsg) {
		this.stateMsg = stateMsg;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMacAddr() {
		return macAddr;
	}

	public void setMacAddr(String macAddr) {
		this.macAddr = macAddr;
	}

	public String getAuthenMsg() {
		return authenMsg;
	}

	public void setAuthenMsg(String authenMsg) {
		this.authenMsg = authenMsg;
	}

	public BaseCommand() {
		
	}
	
	public BaseCommand(String cmd, String ip, String macAddr, String authenMsg, String stateMsg) {
		super();
		this.cmd = cmd;
		this.ip = ip;
		this.macAddr = macAddr;
		this.authenMsg = authenMsg;
		this.stateMsg = stateMsg;
	}

	@Override
	public String toString() {
		return "cmd=" + cmd + ", ip=" + ip + ", macAddr=" + macAddr + ", authenMsg=" + authenMsg + ", stateMsg=" + stateMsg;
	}

//	public abstract String toString();
	
}
