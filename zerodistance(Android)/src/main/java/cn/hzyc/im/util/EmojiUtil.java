package cn.hzyc.im.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hzyc.im.R;

/**
 * 表情功能工具类
 * 
 * @author Jinquan.Wang
 */
public class EmojiUtil {
	
	private static LinkedHashMap<String, Integer> mEmojiMap = new LinkedHashMap<String, Integer>();
	public static ArrayList<Emoji> mEmojiDatas =  new ArrayList<Emoji>();

	//  \\S  匹配任意不是空白符的字符
	//  \\S+ 匹配不包含空白符的字符串
	public static final Pattern PHIZ_URL = Pattern.compile("\\[(\\S+?)\\]");

	public static void setTextWithEmoji(Context context, TextView textView, String message) {
		textView.setText(toSpannableString(context, message), TextView.BufferType.SPANNABLE);
	}

	public static void setTextWithEmoji(Context context, TextView textView, String message, int
			emojiWidth) {
		textView.setText(toSpannableString(context, message, emojiWidth), TextView.BufferType.SPANNABLE);
	}

	private static CharSequence toSpannableString(Context context, String message) {
		if (TextUtils.isEmpty(message) || !message.contains("["))
			return message;

		return toSpannableString(context, message, dp2px(context,20));
	}

	private static int dp2px(Context context, int dp) {
		return (int) (context.getResources().getDisplayMetrics().density * dp);
	}
	/**
	 * 将纯文本转换为可图文混排的字符串
	 *
	 * @param message 要显示的文本内容，图文混排
	 * @param phizWidth 表情显示的大小
	 * @return
	 */
	private static CharSequence toSpannableString(Context context, String message, int phizWidth) {
		if (TextUtils.isEmpty(message)) {
			return "";
		}
		SpannableString spanStr = SpannableString.valueOf(message);
		Matcher matcher = PHIZ_URL.matcher(spanStr);
		while (matcher.find()) {
			int startIndex = matcher.start();
			int endIndex = matcher.end();

			String emojiDesc = matcher.group(0);// 成功匹配的字符串

			// 如果找不到该表情
			if (!mEmojiMap.containsKey(emojiDesc)) {
				continue;
			}

			int emojiResId = mEmojiMap.get(emojiDesc);

			Bitmap newBitmap = createFixSizeBitmap(context, phizWidth, emojiResId);
			ImageSpan localImageSpan = new ImageSpan(context, newBitmap, ImageSpan.ALIGN_BOTTOM);
			spanStr.setSpan(localImageSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

//			int iconResId = mEmojiMap.get(emojiDesc);
//			ImageSpan localImageSpan = new ImageSpan(context, iconResId, ImageSpan.ALIGN_BOTTOM);
//			spanStr.setSpan(localImageSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return spanStr;
	}

	private static Bitmap createFixSizeBitmap(Context context, int phizWidth,
			int emojiResId) {
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), emojiResId);
		int rawHeigh = bitmap.getHeight();
		int rawWidth = bitmap.getWidth();
		int newHeight = phizWidth;
		int newWidth = phizWidth;
		float heightScale = ((float) newHeight) / rawHeigh;
		float widthScale = ((float) newWidth) / rawWidth;
		Matrix matrix = new Matrix();
		matrix.postScale(heightScale, widthScale);
		Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, rawWidth, rawHeigh, matrix, true);
		return newBitmap;
	}

	private  static ArrayList<Emoji> initList( LinkedHashMap<String, Integer> map) {
		ArrayList<Emoji> emojiDatas =  new ArrayList<Emoji>();
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			Emoji emoji = new Emoji(entry.getValue(), entry.getKey());
			emojiDatas.add(emoji);
		}
		return emojiDatas;
	}

	public static class Emoji {
		public int resId;
		public String desc;

		public Emoji(int resId, String desc) {
			this.resId = resId;
			this.desc = desc;
		}
	}

	static {
		mEmojiMap.put("[微笑]", R.drawable.smiley_0);
		mEmojiMap.put("[伤心]", R.drawable.smiley_1);
		mEmojiMap.put("[美女]", R.drawable.smiley_2);
		mEmojiMap.put("[发呆]", R.drawable.smiley_3);
		mEmojiMap.put("[墨镜]", R.drawable.smiley_4);
		mEmojiMap.put("[大哭]", R.drawable.smiley_5);
		mEmojiMap.put("[害羞]", R.drawable.smiley_6);
		mEmojiMap.put("[闭嘴]", R.drawable.smiley_7);
		mEmojiMap.put("[睡觉]", R.drawable.smiley_8);
		mEmojiMap.put("[伤心]", R.drawable.smiley_9);

		mEmojiMap.put("[冷汗]", R.drawable.smiley_10);
		mEmojiMap.put("[发怒]", R.drawable.smiley_11);
		mEmojiMap.put("[调皮]", R.drawable.smiley_12);
		mEmojiMap.put("[呲牙]", R.drawable.smiley_13);
		mEmojiMap.put("[惊讶]", R.drawable.smiley_14);
		mEmojiMap.put("[难过]", R.drawable.smiley_15);
		mEmojiMap.put("[酷]", R.drawable.smiley_16);
		mEmojiMap.put("[汗]", R.drawable.smiley_17);
		mEmojiMap.put("[抓狂]", R.drawable.smiley_18);
		mEmojiMap.put("[吐]", R.drawable.smiley_19);

		mEmojiMap.put("[偷笑]", R.drawable.smiley_20);
		mEmojiMap.put("[快乐]", R.drawable.smiley_21);
		mEmojiMap.put("[奇]", R.drawable.smiley_22);
		mEmojiMap.put("[傲]", R.drawable.smiley_23);
		mEmojiMap.put("[饿]", R.drawable.smiley_24);
		mEmojiMap.put("[累]", R.drawable.smiley_25);
		mEmojiMap.put("[惊恐]", R.drawable.smiley_26);
		mEmojiMap.put("[汗]", R.drawable.smiley_27);
		mEmojiMap.put("[高兴]", R.drawable.smiley_28);
		mEmojiMap.put("[大兵]", R.drawable.smiley_29);

		mEmojiMap.put("[奋斗]", R.drawable.smiley_30);
		mEmojiMap.put("[骂]", R.drawable.smiley_31);
		mEmojiMap.put("[疑问]", R.drawable.smiley_32);
		mEmojiMap.put("[嘘]", R.drawable.smiley_33);
		mEmojiMap.put("[晕]", R.drawable.smiley_34);
		mEmojiMap.put("[痛苦]", R.drawable.smiley_35);
		mEmojiMap.put("[衰]", R.drawable.smiley_36);
		mEmojiMap.put("[鬼]", R.drawable.smiley_37);
		mEmojiMap.put("[敲打]", R.drawable.smiley_38);
		mEmojiMap.put("[再见]", R.drawable.smiley_39);

		mEmojiMap.put("[冷汗]", R.drawable.smiley_40);
		mEmojiMap.put("[挖鼻]", R.drawable.smiley_41);
		mEmojiMap.put("[鼓掌]", R.drawable.smiley_42);
		mEmojiMap.put("[出丑]", R.drawable.smiley_43);
		mEmojiMap.put("[坏笑]", R.drawable.smiley_44);
		mEmojiMap.put("[左嘘]", R.drawable.smiley_45);
		mEmojiMap.put("[右嘘]", R.drawable.smiley_46);
		mEmojiMap.put("[打哈欠]", R.drawable.smiley_47);
		mEmojiMap.put("[鄙视]", R.drawable.smiley_48);
		mEmojiMap.put("[委屈]", R.drawable.smiley_49);

		mEmojiMap.put("[快哭了]", R.drawable.smiley_50);
		mEmojiMap.put("[邪恶]", R.drawable.smiley_51);
		mEmojiMap.put("[亲亲]", R.drawable.smiley_52);
		mEmojiMap.put("[吓吓]", R.drawable.smiley_53);
		mEmojiMap.put("[可怜]", R.drawable.smiley_54);
		mEmojiMap.put("[菜刀]", R.drawable.smiley_55);
		mEmojiMap.put("[西瓜]", R.drawable.smiley_56);
		mEmojiMap.put("[啤酒]", R.drawable.smiley_57);
		mEmojiMap.put("[篮球]", R.drawable.smiley_58);
		mEmojiMap.put("[乒乓球]", R.drawable.smiley_59);

		mEmojiMap.put("[喝茶]", R.drawable.smiley_60);
		mEmojiMap.put("[吃饭]", R.drawable.smiley_61);
		mEmojiMap.put("[猪头]", R.drawable.smiley_62);
		mEmojiMap.put("[鲜花]", R.drawable.smiley_63);
		mEmojiMap.put("[花谢]", R.drawable.smiley_64);
		mEmojiMap.put("[吻]", R.drawable.smiley_65);
		mEmojiMap.put("[红心]", R.drawable.smiley_66);
		mEmojiMap.put("[心碎]", R.drawable.smiley_67);
		mEmojiMap.put("[生日]", R.drawable.smiley_68);
		mEmojiMap.put("[闪电]", R.drawable.smiley_69);

		mEmojiMap.put("[地雷]", R.drawable.smiley_70);
		mEmojiMap.put("[刀]", R.drawable.smiley_71);
		mEmojiMap.put("[足球]", R.drawable.smiley_72);
		mEmojiMap.put("[甲虫]", R.drawable.smiley_73);
		mEmojiMap.put("[便便]", R.drawable.smiley_74);
		mEmojiMap.put("[月亮]", R.drawable.smiley_75);
		mEmojiMap.put("[太阳]", R.drawable.smiley_76);
		mEmojiMap.put("[礼物]", R.drawable.smiley_77);
		mEmojiMap.put("[拥抱]", R.drawable.smiley_78);
		mEmojiMap.put("[强]", R.drawable.smiley_79);

		mEmojiMap.put("[弱]", R.drawable.smiley_80);
		mEmojiMap.put("[握手]", R.drawable.smiley_81);
		mEmojiMap.put("[胜利]", R.drawable.smiley_82);
		mEmojiMap.put("[抱拳]", R.drawable.smiley_83);
		mEmojiMap.put("[勾引]", R.drawable.smiley_84);
		mEmojiMap.put("[握拳]", R.drawable.smiley_85);
		mEmojiMap.put("[差劲]", R.drawable.smiley_86);
		mEmojiMap.put("[爱你]", R.drawable.smiley_87);
		mEmojiMap.put("[No]", R.drawable.smiley_88);
		mEmojiMap.put("[OK]", R.drawable.smiley_89);

		mEmojiMap.put("[爱情]", R.drawable.smiley_90);
		mEmojiMap.put("[飞吻]", R.drawable.smiley_91);
		mEmojiMap.put("[跳跳]", R.drawable.smiley_92);
		mEmojiMap.put("[发抖]", R.drawable.smiley_93);
		mEmojiMap.put("[怄火]", R.drawable.smiley_94);
		mEmojiMap.put("[转圈]", R.drawable.smiley_95);
		mEmojiMap.put("[磕头]", R.drawable.smiley_96);
		mEmojiMap.put("[回头]", R.drawable.smiley_97);
		mEmojiMap.put("[跳绳]", R.drawable.smiley_98);
		mEmojiMap.put("[投降]", R.drawable.smiley_99);

		mEmojiMap.put("[激动]", R.drawable.smiley_100);
		mEmojiMap.put("[乱舞]", R.drawable.smiley_101);
		mEmojiMap.put("[献吻]", R.drawable.smiley_102);
		mEmojiMap.put("[左太极]", R.drawable.smiley_103);
		mEmojiMap.put("[右太极]", R.drawable.smiley_104);

		mEmojiDatas = initList(mEmojiMap);
	}
}
