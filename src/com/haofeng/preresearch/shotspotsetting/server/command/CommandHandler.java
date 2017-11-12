package com.haofeng.preresearch.shotspotsetting.server.command;

public class CommandHandler {
	
	/**
	 * 封装接收到的字符串命令
	 * @param receivedCmd
	 * @return
	 */
	public static BaseCommand packageReceivedCmd(String receivedCmd){
		BaseCommand command = null;
		
		String[] cmds = receivedCmd.split(",");
		if(cmds.length == 5){
			String cmd = cmds[0].split("=")[1];
			String ip = cmds[1].split("=")[1];
			String macAddr = cmds[2].split("=")[1];
			String authenMsg = cmds[3].split("=")[1];
			String stateMsg = cmds[4].split("=")[1];
			
			command = new BaseCommand(cmd, ip, macAddr, authenMsg, stateMsg);
		}
		
		return command;
	}
	
	/**
	 * 解析命令为字符串
	 * @param command
	 * @return
	 */
	public static String parsePackagedCmd(BaseCommand command){
		String cmd = "";
		cmd = command.toString();
		
		return cmd;
	}
}
