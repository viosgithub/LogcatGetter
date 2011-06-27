package foo.sample.LogcatGetter;

interface ILogcatGetterService {
	 java.util.List<String> getDispData();
	 int saveLog(String FileName);
	 void stopWrite();
	 void setLogBreak();
}
