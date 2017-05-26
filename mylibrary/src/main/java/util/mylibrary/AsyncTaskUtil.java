package util.mylibrary;

import android.os.AsyncTask;

/**
 * 异步任务工具类
 * @author dpb
 *
 */
public class AsyncTaskUtil extends AsyncTask<Void, Integer, Object>{
	
	
	private Request request;
	private RequestCallBack requestCallBack;
	
	public AsyncTaskUtil(Request request, RequestCallBack requestCallBack) {
		super();
		if(request == null || requestCallBack == null){
			throw new NullPointerException("request or requestCallBack is not be null ....");
		}
		this.request = request;
		this.requestCallBack = requestCallBack;
	}

	@Override
	protected Object doInBackground(Void... params) {
		// TODO Auto-generated method stub
		return request.doRequest();
	}
	
	@Override
	protected void onPostExecute(Object obj) {
		// TODO Auto-generated method stub
		super.onPostExecute(obj);
		if(obj != null){
			requestCallBack.sucess(obj);
		}else{
			requestCallBack.equals("网络操作异常，请检查！！！");
		}
		
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
	}

	public interface Request{
		Object doRequest();
	}
	public interface RequestCallBack{
		
		void sucess(Object obj);
		void error(String msg);
		
	}

}
