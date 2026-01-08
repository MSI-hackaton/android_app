package com.msi.android.ui.helper;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.msi.android.R;

public class StageNavigationHelper {

    public static void setupStageButtons(Fragment fragment, View view) {
        view.findViewById(R.id.btn_stage_preparation).setOnClickListener(v ->
                NavHostFragment.findNavController(fragment).navigate(R.id.preparationFragment)
        );

        view.findViewById(R.id.btn_stage_construction).setOnClickListener(v ->
                NavHostFragment.findNavController(fragment).navigate(R.id.constructionFragment)
        );

        view.findViewById(R.id.btn_stage_acceptance).setOnClickListener(v ->
                NavHostFragment.findNavController(fragment).navigate(R.id.acceptanceFragment)
        );

//        view.findViewById(R.id.btn_stage_warranty).setOnClickListener(v ->
//                NavHostFragment.findNavController(fragment).navigate(R.id.warrantyFragment)
//        );
    }
}
