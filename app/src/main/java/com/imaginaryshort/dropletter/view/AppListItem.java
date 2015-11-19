package com.imaginaryshort.dropletter.view;

public class AppListItem {
   private String appName;
   private int count, importance;

   public String getAppName() {
      return appName;
   }

   public void setAppName(String appName) {
      this.appName = appName;
   }

   public int getCount() {
      return count;
   }

   public void setCount(int count) {
      this.count = count;
   }

   public int getImportance() {
      return importance;
   }

   public void setImportance(int importance) {
      this.importance = importance;
   }
}
