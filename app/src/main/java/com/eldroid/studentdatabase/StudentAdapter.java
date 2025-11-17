package com.eldroid.studentdatabase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.activity.result.ActivityResultLauncher;
import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    private final Context context;
    private final List<Student> students;     // filtered list
    private final List<Student> allStudents;  // original list
    private final ActivityResultLauncher<Intent> editLauncher;
    private final StudentDatabaseHelper dbHelper;

    public StudentAdapter(Context ctx, List<Student> list, ActivityResultLauncher<Intent> launcher) {
        context = ctx;
        students = new ArrayList<>(list);
        allStudents = new ArrayList<>(list);
        editLauncher = launcher;
        dbHelper = new StudentDatabaseHelper(ctx);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_student, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        Student s = students.get(pos);

        h.name.setText(s.getName());
        h.course.setText(s.getCourse());

        if (s.getImageUri() != null && !s.getImageUri().isEmpty())
            h.image.setImageURI(Uri.parse(s.getImageUri()));
        else
            h.image.setImageResource(R.drawable.ic_person);

        // open edit page
        h.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, MainActivity2.class);
            i.putExtra("id", s.getId());
            editLauncher.launch(i);
        });

        // more button
        h.moreBtn.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, h.moreBtn);
            popup.inflate(R.menu.action_btns);
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_edit) {
                    Intent i = new Intent(context, MainActivity2.class);
                    i.putExtra("id", s.getId());
                    editLauncher.launch(i);
                    return true;
                }
                if (item.getItemId() == R.id.action_delete) {
                    dbHelper.deleteStudent(s.getId());
                    students.remove(pos);
                    notifyItemRemoved(pos);
                    Toast.makeText(context, "Student deleted", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    // ------------------------------
    // ✔ FILTER NAME ONLY
    // ------------------------------
    @SuppressLint("NotifyDataSetChanged")
    public void filter(String text) {
        students.clear();

        if (text == null || text.trim().isEmpty()) {
            students.addAll(allStudents);
        } else {
            String query = text.toLowerCase();
            for (Student s : allStudents) {
                if (s.getName().toLowerCase().contains(query)) {
                    students.add(s);
                }
            }
        }

        notifyDataSetChanged();
    }

    public List<Student> getStudentList() {
        return students;
    }

    // Refresh list after delete/edit
    @SuppressLint("NotifyDataSetChanged")
    public void refreshData(List<Student> newList) {
        students.clear();
        allStudents.clear();

        students.addAll(newList);
        allStudents.addAll(newList);

        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image, moreBtn;
        TextView name, course;

        ViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.imgProfile);
            moreBtn = v.findViewById(R.id.moreBtn);
            name = v.findViewById(R.id.txtName);
            course = v.findViewById(R.id.txtCourse);
        }
    }
}
