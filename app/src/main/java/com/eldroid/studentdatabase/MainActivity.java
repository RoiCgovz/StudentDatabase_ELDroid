package com.eldroid.studentdatabase;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    StudentAdapter adapter;
    List<Student> studentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Search Bar
        SearchView searchView = findViewById(R.id.searchView);
        EditText edit = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        edit.setTextColor(Color.BLACK);
        edit.setHintTextColor(Color.LTGRAY);
        for (int id : new int[]{androidx.appcompat.R.id.search_mag_icon, androidx.appcompat.R.id.search_close_btn}) {
            ((ImageView) searchView.findViewById(id)).setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            edit.setTextCursorDrawable(R.drawable.customcursor);

        // Title Bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // RecyclerView
        recyclerView = findViewById(R.id.studentRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Dummy student data
        studentList.add(new Student("Alpha", "BSCS", R.drawable.ic_person));
        studentList.add(new Student("Bravo", "BSIT", R.drawable.ic_person));
        studentList.add(new Student("Charlie", "BSCS", R.drawable.ic_person));
        studentList.add(new Student("Delta", "BSIT", R.drawable.ic_person));

        // Adapter
        adapter = new StudentAdapter(this, studentList);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)
        {
            @Override
            public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder from, @NonNull RecyclerView.ViewHolder to)
            {
                int fromPos = from.getAdapterPosition(), toPos = to.getAdapterPosition();
                Collections.swap(studentList, fromPos, toPos);
                adapter.notifyItemMoved(fromPos, toPos);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int dir)
            {
                int pos = viewHolder.getAdapterPosition();
                String name = studentList.get(pos).getName();
                Toast.makeText(MainActivity.this, "Deleted: " + name, Toast.LENGTH_SHORT).show();
                studentList.remove(pos);
                adapter.notifyItemRemoved(pos);
            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.additem) {
            Toast.makeText(this, "Add item", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity2.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
