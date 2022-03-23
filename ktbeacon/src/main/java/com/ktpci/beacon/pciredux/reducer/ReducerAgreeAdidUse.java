//package com.ktpci.beacon.pciredux.reducer;
//
//import androidx.annotation.NonNull;
//
//import com.ktpci.beacon.pciredux.action.ActionAgreeAdidUse;
//import com.ktpci.beacon.pciredux.core.Action;
//import com.ktpci.beacon.pciredux.core.PCIReducer;
//import com.ktpci.beacon.pciredux.state.PCIState;
//import com.ktpci.beacon.pciutil.PCILog;
//
//public class ReducerAgreeAdidUse implements PCIReducer {
//
//    @NonNull
//    @Override
//    public PCIState reduce(@NonNull PCIState currentState, @NonNull Action action) {
//
//        ActionAgreeAdidUse actionAgreeAdidUse = (ActionAgreeAdidUse) action;
//
//        try {
//            if (!currentState.isAdidUseAgreed()) {
//                currentState.setAdidUseAgreed(true);
//            }
//        } catch (Exception e) {
//            PCILog.e(e);
//        }
//
//        return currentState;
//    }
//}
