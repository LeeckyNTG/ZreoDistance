package cn.em.sdk.bean.pinyin;

import java.util.Comparator;

import cn.em.sdk.bean.EaseUser;

public class PinyinComparator implements Comparator<EaseUser> {

	public int compare(EaseUser o1, EaseUser o2) {
		if (o2.getInitialLetter().equals("#")) {
			return -1;
		} else if (o1.getInitialLetter().equals("#")) {
			return 1;
		} else {
			return o1.getInitialLetter().compareTo(o2.getInitialLetter());
		}
	}
	
}
