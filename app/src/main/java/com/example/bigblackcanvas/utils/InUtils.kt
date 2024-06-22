package com.example.bigblackcanvas.utils

import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.icu.util.Calendar
import java.text.SimpleDateFormat


class InUtils {

    companion object{
        val TODOS = "todos"
        val APPS = "apps"
        val DATA = "data"
        val APP_SEPARATOR = ":"

        fun getInstalledApps(pkgManager: PackageManager): List<ResolveInfo>{
            val exludedActivities = listOf<String>("Paramètres", "Big Black Canvas");
            var activities = pkgManager.queryIntentActivities(
                Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER),
                0
            )

            activities = activities.filter {
                it.loadLabel(pkgManager) !in exludedActivities
            }
            activities.sortWith(ResolveInfo.DisplayNameComparator(pkgManager))
            return activities;
        }

        fun displayTodos(todos: List<String>): String{
            return todos.joinToString(separator = "\n- ")
        }

        fun getListFromShp(shp: SharedPreferences, data: String): List<String> {
            val listString = shp.getString(data, "")
            return listString?.split(",")?.toList()?.sortedBy { it } ?: ArrayList()
        }

        fun removeEltFromListInShp(shp: SharedPreferences, data: String, elt: String){
            var list = getListFromShp(shp, data).toMutableList()
            list = list.filter { !it.equals(elt) }.toMutableList()
            val editor = shp.edit()
            editor.putString(data, list.joinToString(","))
            editor.apply()
        }

        fun addEltIntoListInShp(shp: SharedPreferences, data: String, elt: String){
            val list = getListFromShp(shp, data).toMutableList()
            if(!list.contains(elt)) list.add(elt) else return
            val editor = shp.edit()
            editor.putString(data, list.joinToString(","))
            editor.apply()
        }

        fun removeEltFromListInShpByIndex(shp: SharedPreferences, data: String, index: Int){
            val list = getListFromShp(shp, data).toMutableList()
            list.removeAt(index)
            val editor = shp.edit()
            editor.putString(data, list.joinToString(","))
            editor.apply()
        }

        fun isWorkingTime(): Boolean{
            val format = SimpleDateFormat("HH:mm:ss")

            fun stringToCalendar(str: String): Calendar{
                val sortie = Calendar.getInstance()
                sortie.time = format.parse(str)
                sortie.add(Calendar.DATE, 1)
                return sortie
            }

            val startTime = stringToCalendar("08:00:00")
            val stopTime = stringToCalendar("18:00:00")
            val now = stringToCalendar(format.format(Calendar.getInstance().time))

            return now.after(startTime) && now.before(stopTime)
        }

        fun buildLaunchingIntent(packageName: String, activityName: String): Intent{
            val componentName = ComponentName(packageName, activityName)
            val i = Intent(Intent.ACTION_MAIN)

            i.addCategory(Intent.CATEGORY_LAUNCHER)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            i.component = componentName

            return i
        }
    }
}