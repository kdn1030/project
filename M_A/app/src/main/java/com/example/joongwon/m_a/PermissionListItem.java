package com.example.joongwon.m_a;

// listview 에 들어갈 item 으로 클래스 정의 -> PermissionListItem
// Guest 에 대한 정보를 담을 수 있음
public class PermissionListItem {

    String id;
    String name;
    String birthday;
    String permission;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
         this.birthday = birthday;
      }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    // 생성자
    public PermissionListItem(String id, String name, String birthday, String permission) {

        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.permission = permission;
    }
}
