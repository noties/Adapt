package io.noties.adapt.sample;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import io.noties.adapt.next.Adapt;
import io.noties.adapt.next.Item;
import io.noties.adapt.next.ItemList;
import io.noties.adapt.next.listview.AdaptListView;
import io.noties.adapt.next.listview.ListViewItem;
import io.noties.adapt.next.recyclerview.AdaptRecyclerView;
import io.noties.adapt.next.recyclerview.AsyncDiffUtilDataSetChangedHandler;
import io.noties.adapt.next.recyclerview.DiffUtilDataSetChangedHandler;

public class NextActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        final Adapt adapt = listView();
        final Adapt adapt = recyclerView();

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {

            int count = 0;

            @Override
            public void onClick(View v) {
                if (count == 0) {
                    final ItemList list = new ItemList();
                    for (int i = 0; i < 10; i++) {
                        list.add(new StringItem("String #" + (i + 1)));
                    }
                    adapt.setItems(list);
                } else if (count == 1) {
                    final ItemList list = new ItemList(adapt.items());
                    for (int i = 0; i < 20; i++) {
                        list.add(new NumberItem(i + 1));
                    }
                    Collections.shuffle(list);
                    adapt.setItems(list);
                } else if (count == 2) {
                    final Calendar calendar = Calendar.getInstance();
                    final ItemList list = new ItemList(adapt.items());
                    for (int i = 0; i < 30; i++) {
                        calendar.add(Calendar.DAY_OF_MONTH, i);
                        list.add(new DateItem(calendar.getTime(), new DateItem.OnClick() {
                            @Override
                            public void onClick(@NonNull Date date) {
                                Toast.makeText(NextActivity.this, date.toString(), Toast.LENGTH_LONG).show();
                            }
                        }));
                    }
                    Collections.shuffle(list);
                    adapt.setItems(list);
                } else {
                    final ItemList list = new ItemList(adapt.items());
                    Collections.shuffle(list);
                    adapt.setItems(list);
                }

                count += 1;
            }
        });
    }

    @NonNull
    private Adapt listView() {
        setContentView(R.layout.activity_list_view);

        final ListView listView = findViewById(R.id.list_view);

        return AdaptListView.attach(listView, new AdaptListView.Configurator() {
            @Override
            public void configure(@NonNull AdaptListView.Configuration configuration) {
                configuration.areAllItemsEnabled(false);
            }
        });
    }

    @NonNull
    private Adapt recyclerView() {
        setContentView(R.layout.activity_recycler_view);

        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        return AdaptRecyclerView.attach(recyclerView, new AdaptRecyclerView.Configurator() {
            @Override
            public void configure(@NonNull AdaptRecyclerView.Configuration configuration) {
                configuration.dataSetChangeHandler(AsyncDiffUtilDataSetChangedHandler.create(
                        DiffUtilDataSetChangedHandler.create(true)
                ));
            }
        });
    }

    private static class StringItem extends Item<StringItem.Holder> implements ListViewItem {

        private final String string;

        protected StringItem(@NonNull String string) {
            super(string.hashCode());
            this.string = string;
        }

        @NonNull
        @Override
        public Holder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
            return new Holder(inflater.inflate(android.R.layout.simple_list_item_1, parent, false));
        }

        @Override
        public void render(@NonNull Holder holder) {
            holder.textView.setText(string);
        }

        @Override
        public boolean listViewIsEnabled() {
            return false;
        }

        static class Holder extends Item.Holder {

            final TextView textView;

            Holder(@NonNull View itemView) {
                super(itemView);
                textView = requireView(android.R.id.text1);
            }
        }
    }

    private static class NumberItem extends Item<NumberItem.Holder> {

        private final int number;

        protected NumberItem(int number) {
            super(number);
            this.number = number;
        }

        @NonNull
        @Override
        public Holder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
            return new Holder(inflater.inflate(android.R.layout.simple_list_item_2, parent, false));
        }

        @Override
        public void render(@NonNull Holder holder) {
            holder.text1.setText(String.valueOf(number));
            holder.text2.setText("Number");
        }

        static class Holder extends Item.Holder {

            final TextView text1;
            final TextView text2;

            Holder(@NonNull View itemView) {
                super(itemView);
                text1 = requireView(android.R.id.text1);
                text2 = requireView(android.R.id.text2);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            NumberItem that = (NumberItem) o;

            return number == that.number;
        }

        @Override
        public int hashCode() {
            return number;
        }
    }

    private static class DateItem extends Item<DateItem.Holder> {

        interface OnClick {
            void onClick(@NonNull Date date);
        }

        private final Date date;
        private final OnClick onClick;

        private DateItem(@NonNull Date date, @NonNull OnClick onClick) {
            super(date.getTime());
            this.date = date;
            this.onClick = onClick;
        }

        @NonNull
        @Override
        public Holder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
            final Holder holder = new Holder(inflater.inflate(android.R.layout.simple_list_item_1, parent, false));
            holder.itemView().setBackgroundColor(Color.YELLOW);
            return holder;
        }

        @Override
        public void render(@NonNull Holder holder) {
            holder.textView.setText(date.toString());
            holder.itemView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClick.onClick(date);
                }
            });
        }

        static class Holder extends Item.Holder {
            final TextView textView;

            Holder(@NonNull View itemView) {
                super(itemView);
                this.textView = requireView(android.R.id.text1);
            }
        }
    }
}
