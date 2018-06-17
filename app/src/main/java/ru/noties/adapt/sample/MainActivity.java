package ru.noties.adapt.sample;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.noties.adapt.Adapt;
import ru.noties.adapt.Holder;
import ru.noties.adapt.ItemView;
import ru.noties.adapt.ViewProcessor;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Adapt<CharSequence> adapt = Adapt.builder(CharSequence.class)
                .include(String.class, new StringView())
                .include(String.class, new CharSequenceView())
                .include(String.class, new CharSequenceView(), new ViewProcessor<String>() {
                    @Override
                    public void process(@NonNull String item, @NonNull View view) {
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                
                            }
                        });
                    }
                })
//                .include(CharSequence.class, new StringView())
                .build();
    }

    private static class StringView extends ItemView<String, StringView.StringHolder> {

        @NonNull
        @Override
        public StringHolder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
            return new StringHolder(inflater.inflate(0, parent, false));
        }

        @Override
        public void bindHolder(@NonNull StringHolder holder, @NonNull String item) {
            // blah
        }

        static class StringHolder extends Holder {

            public StringHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }

    private static class CharSequenceView extends ItemView<CharSequence, Holder> {

        @NonNull
        @Override
        public Holder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
            return null;
        }

        @Override
        public void bindHolder(@NonNull Holder holder, @NonNull CharSequence item) {

        }
    }
}
