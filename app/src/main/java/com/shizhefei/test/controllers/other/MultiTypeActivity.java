package com.shizhefei.test.controllers.other;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.shizhefei.mvc.MVCCoolHelper;
import com.shizhefei.recyclerview.HFAdapter;
import com.shizhefei.test.models.datasource.MyDataSource;
import com.shizhefei.test.models.enties.Book;
import com.shizhefei.test.view.adapters.multitype.Message;
import com.shizhefei.test.view.adapters.multitype.MultiTypeDataAdapter;
import com.shizhefei.test.view.adapters.multitype.provider.BookProvider;
import com.shizhefei.test.view.adapters.multitype.provider.MessageProvider;
import com.shizhefei.view.coolrefreshview.CoolRefreshView;
import com.shizhefei.view.coolrefreshview.header.DefaultHeader;
import com.shizhefei.view.multitype.ItemBinderFactory;
import com.shizhefei.view.multitype.ItemViewProviderSet;
import com.shizhefei.view.mvc.demo.R;

import java.util.List;


public class MultiTypeActivity extends Activity {
    private CoolRefreshView coolRefreshView;
    private RecyclerView recyclerView;
    private MVCCoolHelper<List<Object>> mvcHelper;
    private String myUserId = "1";
    private View backButton;
    private MultiTypeDataAdapter<Object> multiTypeDataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_type);

        backButton = findViewById(R.id.button1);
        coolRefreshView = (CoolRefreshView) findViewById(R.id.multiType_coolRefreshView);
        recyclerView = (RecyclerView) findViewById(R.id.multiType_recyclerView);

        coolRefreshView.setPullHeader(new DefaultHeader());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemBinderFactory factory = new ItemBinderFactory();
        factory.registerProvider(Book.class, new BookProvider());
        factory.registerProvider(Message.class, new ItemViewProviderSet<Message>(new MessageProvider(MessageProvider.ALIGN_LEFT), new MessageProvider(MessageProvider.ALIGN_RIGHT)) {
            @Override
            protected int selectIndex(Message message) {
                return myUserId.equals(message.userId) ? 1 : 0;
            }
        });
        multiTypeDataAdapter = new MultiTypeDataAdapter<>(factory);
//        adapter.addHeader(view);
        multiTypeDataAdapter.setOnItemClickListener(OnItemClickListener);

        mvcHelper = new MVCCoolHelper<>(coolRefreshView);
        mvcHelper.setDataSource(new MyDataSource());
        mvcHelper.setAdapter(multiTypeDataAdapter);
        mvcHelper.refresh();

        backButton.setOnClickListener(OnClickListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mvcHelper.destory();
    }

    private HFAdapter.OnItemClickListener OnItemClickListener = new HFAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(HFAdapter adapter, RecyclerView.ViewHolder vh, int position) {
            Object object = multiTypeDataAdapter.getData().get(position);
            Toast.makeText(getApplicationContext(), object.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener OnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == backButton) {
                finish();
            }
        }
    };
}
