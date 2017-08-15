package com.system.ui.interpret;

/**
 * 命令表达式
 */
public abstract class CommandExpression {
	
	protected String mExpression;

	public CommandExpression(String expression){
		this.mExpression = expression;
	}

	protected String interpret(){
		return mExpression;
	}
}
