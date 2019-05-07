package com.jianzhong.lyag.db.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.jianzhong.lyag.db.DatabaseHelper;
import com.jianzhong.lyag.model.CourseAudioRecorModel;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by zhengwencheng on 2018/1/27.
 * E-mail 15889964542@163.com
 *
 * 课程音频播放记录的dao
 */
public class CourseAudioRecordDao {
    private DatabaseHelper helper;
    private Dao<CourseAudioRecorModel,Integer> dao;

    public CourseAudioRecordDao(Context context){
        try {
            helper = DatabaseHelper.getHelper(context);
            dao = helper.getDao(CourseAudioRecorModel.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //批量插入数据
    public int insertList(List<CourseAudioRecorModel> models){
        try {
            return dao.create(models);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //插入单条数据
    public int insert(CourseAudioRecorModel model){
        try {
            return dao.create(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //删除某一条数据
    public int delete(CourseAudioRecorModel model){
        try {
            return dao.delete(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //删除全部数据
    public int deleteForAll(List<CourseAudioRecorModel> models){
        try {
            return dao.delete(models);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    //更改数据
    public int update(CourseAudioRecorModel model){
        try {
            return dao.update(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //查询数据 这里只查询全部 因为只有keyword
    public List<CourseAudioRecorModel> queryForAll(){
        List<CourseAudioRecorModel> list = null;
        try {
            list = dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    //查询数据 这里只查一条
    public List<CourseAudioRecorModel> queryForValue(Map<String,Object> map){
        List<CourseAudioRecorModel> list = null;
        try {
            list = dao.queryForFieldValues(map);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
