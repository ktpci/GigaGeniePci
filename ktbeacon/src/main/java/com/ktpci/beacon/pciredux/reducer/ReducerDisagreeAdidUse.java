//package com.ktpci.beacon.pciredux.reducer;
//
//import androidx.annotation.NonNull;
//
//import com.ktpci.beacon.pciredux.action.ActionDisagreeAdidUse;
//import com.ktpci.beacon.pciredux.core.Action;
//import com.ktpci.beacon.pciredux.core.PCIReducer;
//import com.ktpci.beacon.pciredux.state.PCIState;
//import com.ktpci.beacon.pciutil.PCILog;
//
//public class ReducerDisagreeAdidUse implements PCIReducer {
//
//    @NonNull
//    @Override
//    public PCIState reduce(@NonNull PCIState currentState, @NonNull Action action) {
//
//        ActionDisagreeAdidUse actionDisagreeAdidUse = (ActionDisagreeAdidUse) action;
//
//        try {
//            if (currentState.isAdidUseAgreed()) {
//                currentState.setAdidUseAgreed(false);
//
//            }
//        } catch (Exception e) {
//            PCILog.e(e);
//        }
//
//        return currentState;
//    }
//}