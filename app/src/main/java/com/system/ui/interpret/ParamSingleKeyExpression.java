package com.system.ui.interpret;

/**
 * 参数键，此键必不含值
 */
public class ParamSingleKeyExpression extends CommandExpression {
	
	public ParamSingleKeyExpression(String expression) {
		super(expression);
	}
	
	@Override
	protected String interpret() {
		return super.interpret();
	}

}
