package com.jianzhong.lyag.ui.interact;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baselib.util.GsonUtils;
import com.baselib.util.ListUtils;
import com.baselib.util.ResultList;
import com.baselib.util.ToastUtils;
import com.jianzhong.lyag.R;
import com.jianzhong.lyag.adapter.HelpAdapter;
import com.jianzhong.lyag.base.BaseRecyclerViewActivity;
import com.jianzhong.lyag.http.HttpCallBack;
import com.jianzhong.lyag.http.HttpConfig;
import com.jianzhong.lyag.http.HttpRequest;
import com.jianzhong.lyag.model.HelpModel;
import com.jianzhong.lyag.model.HelpSortFieldModel;
import com.jianzhong.lyag.model.TagModel;
import com.jianzhong.lyag.util.PopWindowUtil;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * 求助列表
 * Created by Max on 2018/3/18.
 */

public class InteractActivity extends BaseRecyclerViewActivity {

    @BindView(R.id.tv_sort)
    TextView mTvSort;
    @BindView(R.id.tv_classify)
    TextView mTvClassify;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.ptr_frame)
    PtrClassicFrameLayout mPtrFrame;
    @BindView(R.id.ll_sort)
    LinearLayout mLlSort;

    private List<HelpSortFieldModel> mHelpSortFieldModels = new ArrayList<>();
    private List<TagModel> mTagModels = new ArrayList<>();
    private String tag_id = "";
    private String order_by = "";
    private int pageIndex = 1;
    private List<HelpModel> mList = new ArrayList<>();
    private HelpAdapter mHelpAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_interact);
        ButterKnife.bind(this);
    }

    @Override
    protected RecyclerView.Adapter getRecyclerViewAdapter() {
        mHelpAdapter = new HelpAdapter(mContext,mList);
        return mHelpAdapter;
    }

    @Override
    public void initData() {
        super.initData();

        setHeadTitle("求助");
        showHeadTitle();
        setHeadIvRightBackground(R.drawable.hd_fb);
        showHeadIvRight();

        initRecylerView();
        //
        getHelpSortField();
    }

    /**
     * 初始化列表
     */
    private void initRecylerView() {
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mContext)
                .color(getResources().getColor(R.color.color_bg))
                .sizeResId(R.dimen.default_margin_8)
                .build());
        mPtrFrame.setMode(PtrFrameLayout.Mode.BOTH);

        mPtrFrame.setPtrHandler(new PtrDefaultHandler2() {
            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
                pageIndex++;
                getHelpList();
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                pageIndex = 1;
                mList.clear();
                getHelpList();
            }
        });
    }

    @OnClick({R.id.head_iv_right, R.id.ll_sort, R.id.ll_classify})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.head_iv_right:
                intent = new Intent(mContext, AddHelpActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_sort:
                PopWindowUtil.getInstance().showHelpSortField(InteractActivity.this, mLlSort, mHelpSortFieldModels, mTvSort, new PopWindowUtil.OnHelpSortClickListener() {
                    @Override
                    public void helpSortClick(HelpSortFieldModel item, int position) {
                        pageIndex = 1;
                        mList.clear();
                        order_by = item.getField();
                        getHelpList();
                    }
                });
                break;
            case R.id.ll_classify:
                PopWindowUtil.getInstance().showTagSelect(InteractActivity.this, mLlSort, mTagModels, mTvClassify, new PopWindowUtil.OnTagClickListener() {
                    @Override
                    public void tagClick(TagModel item, int position) {
                        pageIndex = 1;
                        mList.clear();
                        tag_id = item.getTag_id();
                        getHelpList();
                    }
                });
                break;
        }
    }

    /**
     * 排序求助字段列表
     */
    private void getHelpSortField() {
        HttpRequest.getInstance().post(HttpConfig.URL_BASE + HttpConfig.URL_HELP_SORT_FIELD, null, new HttpCallBack() {
            @Override
            public void onSuccess(String s) {
                ResultList<HelpSortFieldModel> resultList = GsonUtils.json2List(s, HelpSortFieldModel.class);
                if (resultList != null && resultList.getCode() == HttpConfig.STATUS_SUCCESS) {
                    if (!ListUtils.isEmpty(resultList.getData())) {
                        mHelpSortFieldModels.addAll(resultList.getData());
                        mTvSort.setText(mHelpSortFieldModels.get(0).getStr());
                        mHelpSortFieldModels.get(0).setIsSelected(1);
                        order_by = mHelpSortFieldModels.get(0).getField();
                    }
                }

                getHelpTag();
            }

            @Override
            public void onFailure(String msg) {
                showErrorView();
            }
        });
    }

    /**
     * 获取求助标签
     */
    private void getHelpTag(){
        HttpRequest.getInstance().post(HttpConfig.URL_BASE + HttpConfig.URL_HELP_TAG, null, new HttpCallBack() {
            @Override
            public void onSuccess(String s) {
                ResultList<TagModel> resultList = GsonUtils.json2List(s,TagModel.class);
                if(resultList != null && resultList.getCode() == HttpConfig.STATUS_SUCCESS){
                    if(!ListUtils.isEmpty(resultList.getData())){
                        mTagModels.addAll(resultList.getData());
                        mTvClassify.setText(mTagModels.get(0).getTag_name());
                        mTagModels.get(0).setIsSelected(1);
                        tag_id = mTagModels.get(0).getTag_id();
                    }
                }

                getHelpList();
            }

            @Override
            public void onFailure(String msg) {
                showErrorView();
            }
        });
    }

    /**
     * 获取求助列表
     */
    private void getHelpList(){
        Map<String,Object> params = new HashMap<>();
        params.put("tag_id",tag_id);
        params.put("order_by",order_by);
        params.put("p",pageIndex + "");
        HttpRequest.getInstance().post(HttpConfig.URL_BASE + HttpConfig.URL_HELP_LIST, params, new HttpCallBack() {
            @Override
            public void onSuccess(String s) {
                ResultList<HelpModel> resultList = GsonUtils.json2List(s,HelpModel.class);
                if(resultList != null && resultList.getCode() == HttpConfig.STATUS_SUCCESS){
                    hideCommonLoading();
                    mPtrFrame.refreshComplete();
                    mList.addAll(resultList.getData());
                    mHelpAdapter.notifyDataSetChanged();
                }else {
                    ToastUtils.show(mContext,resultList != null ? resultList.getMessage():"数据解析错误");
                }
                isShowNoDataView();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtils.show(mContext,msg);
                showErrorView();
            }
        });
    }
}
