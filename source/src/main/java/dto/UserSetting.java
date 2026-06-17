package dto;

import java.io.Serializable;

/**
 * ユーザー設定データ受け渡し
 */
public class UserSetting implements Serializable {
    private static final long serialVersionUID = 1L;

    // 保持するデータ
    private String iconName;
    private String userName;
    private int childCount;
    private String garbageName;
    private String garbageDay;

    // コンストラクタ
    public UserSetting() {}

    // ゲッターとセッター
    public String getIconName() { return iconName; }
    public void setIconName(String iconName) { this.iconName = iconName; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public int getChildCount() { return childCount; }
    public void setChildCount(int childCount) { this.childCount = childCount; }

    public String getGarbageName() { return garbageName; }
    public void setGarbageName(String garbageName) { this.garbageName = garbageName; }

    public String getGarbageDay() { return garbageDay; }
    public void setGarbageDay(String garbageDay) { this.garbageDay = garbageDay; }
}