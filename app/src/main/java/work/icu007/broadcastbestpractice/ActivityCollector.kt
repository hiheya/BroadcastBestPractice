package work.icu007.broadcastbestpractice

import android.app.Activity


/*
 * Author: Charlie_Liao
 * Time: 2023/11/23-17:35
 * E-mail: rookie_l@icu007.work
 * manage all activity
 */

object ActivityCollector {
    private val activites = ArrayList<Activity>()

    // add activities to ArrayList
    fun addActivity(activity: Activity){
        activites.add(activity)
    }

    // remove activities from ArrayList
    fun removeActivity(activity: Activity){
        activites.remove(activity)
    }

    // finish all activities
    fun finishAll(){
        for (activity in activites){
            if (!activity.isFinishing){
                activity.finish()
            }
        }
        activites.clear()
    }
}