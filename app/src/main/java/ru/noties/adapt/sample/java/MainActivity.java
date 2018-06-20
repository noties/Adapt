package ru.noties.adapt.sample.java;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import ru.noties.adapt.Adapt;
import ru.noties.adapt.OnClickViewProcessor;
import ru.noties.adapt.sample.R;
import ru.noties.adapt.sample.core.ItemGenerator;
import ru.noties.adapt.sample.core.item.AppendItem;
import ru.noties.adapt.sample.core.item.Item;
import ru.noties.adapt.sample.core.item.SectionItem;
import ru.noties.adapt.sample.core.item.ShapeItem;
import ru.noties.adapt.sample.java.view.AppendView;
import ru.noties.adapt.sample.java.view.SectionView;
import ru.noties.adapt.sample.java.view.ShapeView;

public class MainActivity extends Activity {

    private final ItemGenerator itemGenerator = ItemGenerator.create();

    private Adapt<Item> adapt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapt = Adapt.builder(Item.class)
                .include(SectionItem.class, new SectionView())
                .include(ShapeItem.class, new ShapeView())
                .include(
                        AppendItem.class,
                        new AppendView(),
                        OnClickViewProcessor.create(new OnClickViewProcessor.OnClick<AppendItem>() {
                            @Override
                            public void onClick(@NonNull AppendItem item, @NonNull View view) {
                                append();
                            }
                        }))
                .build();

        initRecyclerView();

        append();
    }

    private void initRecyclerView() {

        final int shapeViewType = adapt.assignedViewType(ShapeItem.class);
        final int spanCount = getResources().getInteger(R.integer.span_count);
        final int padding = getResources().getDimensionPixelSize(R.dimen.item_padding);

        final GridLayoutManager manager = new GridLayoutManager(this, spanCount);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return shapeViewType == adapt.itemViewType(position)
                        ? 1
                        : spanCount;
            }
        });

        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(padding, padding, padding, padding);
            }
        });
        recyclerView.setClipToPadding(false);
        recyclerView.setClipChildren(false);
        recyclerView.setPadding(padding, padding, padding, padding);

        recyclerView.setAdapter(adapt.recyclerViewAdapter());
    }

    private void append() {
        adapt.setItems(itemGenerator.generate(adapt.getItems()));
    }
}
