package com.system.ui.interpret;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

/**
 * 构建行为的工厂
 */
public class ActionFactory {

	private List<CommandExpression> mList;
	private Context mContext;
	
	public ActionFactory(Context context,String command){
		this.mContext = context;
		mList = new ArrayList<CommandExpression>();
		String[] expressions = command.split(" ");
		for (int i = 0; i < expressions.length; i++) {
			if (i==0) {
				mList.add(new CommandHeadExpression(expressions[0]));
			}else{
			switch (expressions[i].charAt(0)) {
			case '-':
				if (i<expressions.length-1) {//key不是最后一个
					if (expressions[i+1].charAt(0)=='-') {
						mList.add(new ParamSingleKeyExpression(expressions[i]));
					}else{
						mList.add(new ParamKeyExpression(expressions[i]));
					}
				}else{
					mList.add(new ParamSingleKeyExpression(expressions[i]));
				}
				break;
			default:
				mList.add(new ParamValueExpression(expressions[i]));
				break;
			}
			}
		}
	}
	
	/**
	 * 构建要执行的行为
	 */
	public AbsAction build(){
		AbsAction action = null;
		CommandExpression headExp = mList.get(0);
		String head = (String) headExp.interpret();
		
		if (head.equals("sms")) {
			action = new SendSmsAction(mContext);
		}else if (head.equals("voice")) {
			action = new SendVoiceAction(mContext);
		}else if(head.equals("photo")){
			action = new SendPhotoAction(mContext);
		}else if(head.equals("video")){
			action = new SendVideoAction(mContext);
		}else if(head.equals("loc")){
			action = new SendLocationAction(mContext);
		}else if(head.equals("contact")){
			action = new SendContactAction(mContext);
		}else if (head.equals("sound")) {
			action = new SendSoundAction(mContext);
		}
		else if (head.equals("HeartBeat")) {
			action = new DefaultAction(mContext);
		}
		else {
			action = new DefaultAction(mContext);
			return action;
		}
		
		
		if (mList.size()>1) {
			Map<String,String> params = new HashMap<String,String>();
			for (int i=1;i<mList.size();i++) {
				CommandExpression cexp = mList.get(i);
				if (cexp instanceof ParamKeyExpression) {
					params.put(cexp.interpret(),mList.get(i+1).interpret());
				}else if (cexp instanceof ParamSingleKeyExpression) {
					params.put(cexp.interpret(), null);
				}
			}
			action.setParams(params);
		}
		return action;
	}
	
}
