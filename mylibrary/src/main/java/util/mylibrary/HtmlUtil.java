package util.mylibrary;

public class HtmlUtil {
	public static String htmltoStr(String content) {
		if (content == null)
			return "";
		String html = content;

		// html = html.replace( "'", "&apos;");
		html = html.replaceAll("&amp;", "&");
		html = html.replace("&quot;", "\""); // "
		html = html.replace("&nbsp;&nbsp;", "\t");// 替换跳格
		html = html.replace("&nbsp;", " ");// 替换空格
		html = html.replace("&lt;", "<");

		html = html.replaceAll("&gt;", ">");
		html = html.replace("<!--", "");
		html = html.replace("-->", "");
		return html;
	}
}
