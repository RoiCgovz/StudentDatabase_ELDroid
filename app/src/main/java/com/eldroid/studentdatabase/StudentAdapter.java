package com.eldroid.studentdatabase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private Context context;
    private List<Student> studentList;

    public StudentAdapter(Context context, List<Student> studentList) {
        this.context = context;
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);

        // Bind student info
        holder.txtName.setText(student.getName());
        holder.txtCourse.setText(student.getCourse());
        holder.imgProfile.setImageResource(student.getImageResId());

        // Show popup menu when "more" ImageView is clicked
        holder.imgOptions.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.inflate(R.menu.action_btns);

            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.action_edit) {
                    Toast.makeText(v.getContext(), "Edit " + student.getName(), Toast.LENGTH_SHORT).show();
                } else if (id == R.id.action_delete) {
                    Toast.makeText(v.getContext(), "Delete " + student.getName(), Toast.LENGTH_SHORT).show();
                }
                return true;
            });

            popup.show();
        });
    }


    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProfile, imgOptions;
            TextView txtName, txtCourse;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            txtName = itemView.findViewById(R.id.txtName);
            txtCourse = itemView.findViewById(R.id.txtCourse);
            imgOptions = itemView.findViewById(R.id.imgOptions);
        }
    }
}
