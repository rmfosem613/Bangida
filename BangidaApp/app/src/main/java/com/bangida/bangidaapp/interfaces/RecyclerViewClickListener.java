package com.bangida.bangidaapp.interfaces;

public interface RecyclerViewClickListener {
    // 이건 확인을 위해 넣음
    void onItemClick(int position);
    // 누르면 안보이던 수정, 삭제 버튼을 보여줌
    void onLongItemClick(int position);
    // 수정을 위해 adpter에 값을 넘김
    void onEditButtonClick(int position);
    // 삭제를 위해 adpter에 값을 넘김
    void onDeleteButtonClick(int position);
}