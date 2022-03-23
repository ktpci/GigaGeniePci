package com.ktpci.beacon.pciredux.state;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ktpci.beacon.pciredux.core.State;
import com.ktpci.beacon.pciutil.PCIFormatter;
import com.ktpci.beacon.pciutil.PCIStorage;
import com.ktpci.beacon.pciutil.PCIStorageKey;

import static com.ktpci.beacon.pciredux.state.PCIState.Type.DEFAULT;

public class PCIState implements State {

    /* Android Context and ... */
    @NonNull
    protected transient Context context;
    @NonNull protected Type type = DEFAULT;

    /* 각종 ID */
//    @Nullable private String pid = null;         // PCI 플랫폼 개인화 ID : 약관 동의 시 서버로 부터 할당 받음
//    @Nullable private String stbId = null;       // PCI 플랫폼 셋탑 ID : 체크인 시 알게됨
//    @Nullable private String said = null;        // 암호화된 셋탑 SAID : 체크인 시 알게됨
//    @Nullable private String uuid = null;        // UUID : Device 정보를 조합하여 구성함
    @Nullable private String adid = null;        // ADID : Google AD ID
    @Nullable private String partnercode = null;  //제휴가 비콘 minor value
    @Nullable private String runtime = null;  //제휴가 비콘 minor value
//    @Nullable private String phoneNumber = null; // 마스킹된 전화번호
//    @Nullable private String fcmToken = null;    // FCM Token
//    @Nullable private String mac = null;         // Mac Address
//    @Nullable private String otmSuid = null;     // OTM SUID
//    @Nullable private String packageKey = null;  // Package Key
      @NonNull private int pcimode = 0;

    /* 각종 동의 여부 */
    private boolean isTermAgreed = false;

//    private boolean isAdidUseAgreed = false;
//    private boolean isAdPushAgreed = false;
//    private boolean isMicUseAgreed = false;
//    private boolean isBleUseAgreed = false;
    private boolean isOptIn = false;


    protected PCIState(@NonNull PCIState src) {
        this.context = src.context;
        this.type = src.type;

//        this.pid = src.pid;
//        this.stbId = src.stbId;
//        this.said = src.said;
//        this.uuid = src.uuid;
        this.adid = src.adid;
        this.partnercode = src.partnercode;
//        this.phoneNumber = src.phoneNumber;
//        this.fcmToken = src.fcmToken;
//        this.mac = src.mac;
//        this.otmSuid = src.otmSuid;
//        this.packageKey = src.packageKey;

        this.isTermAgreed = src.isTermAgreed;
        this.pcimode = src.pcimode;
//        this.isAdidUseAgreed = src.isAdidUseAgreed;
//        this.isAdPushAgreed = src.isAdPushAgreed;
//        this.isMicUseAgreed = src.isMicUseAgreed;
//        this.isBleUseAgreed = src.isBleUseAgreed;
        this.isOptIn = src.isOptIn;
        this.runtime = src.runtime;

    }

    private PCIState(@NonNull Context context) {
        this.context = context;
    }

    @NonNull
    public static PCIState from(@NonNull Context context) {
        PCIState state = PCIStorage.loadGson(context, PCIStorageKey.STATE).as(PCIState.class);
        if (state == null) return new PCIState(context);
        else {
            state.context = context;
            switch (state.type) {
                case DEFAULT:
                    return new PCIState1Default(state);
                case INACTIVE:
                    return new PCIState2Inactive(state);
                case ACTIVE:
                    return new PCIState3Active(state);
            }
        }
        return state;
    }

    @NonNull
    public static PCIState from(@NonNull PCIState src) {
        PCIState state = new PCIState(src);
        switch (state.type) {
            case DEFAULT:
                return new PCIState1Default(state);
            case INACTIVE:
                return new PCIState2Inactive(state);
            case ACTIVE:
                return new PCIState3Active(state);

            default:
                throw new IllegalStateException("Invalid State Type");
        }
    }
    @SuppressWarnings("PointlessBooleanExpression")
    @NonNull
    public Type maxType() {
        switch (this.type) {
            case DEFAULT:
                return DEFAULT;
            case INACTIVE:
                if (isTermAgreed == false){   //    || isAdidUseAgreed == false) {
                    return Type.DEFAULT;
                }
                else return Type.INACTIVE;
            case ACTIVE:
                if (isOptIn == false){   //    || isAdidUseAgreed == false) {
                    return Type.INACTIVE;
                }
                else return Type.ACTIVE;
            default:
                return DEFAULT;
        }
    }

    /* Android Context */
    @NonNull
    public Context getContext() {
        return context;
    }

    public void setContext(@NonNull Context context) {
        this.context = context;
    }

    public void writePersistent() {
        PCIStorage.saveGson(context, PCIStorageKey.STATE, this);
    }

    public void clearPreference() {
        PCIStorage.remove(context, PCIStorageKey.STATE);
    }

    @Nullable
    public String getPackageName() {
        return context.getPackageName();
    }


    /* 각종 ID */

//    @Nullable
//    public String getUuid() {
//        return uuid;
//    }
//    public void setUuid(@Nullable String uuid) {
//        this.uuid = PCIFormatter.nullIfEmpty(uuid);
//    }

    @Nullable
    public String getAdid() {
        return adid;
    }
    public void setAdid(@Nullable String adid) {
        this.adid = PCIFormatter.nullIfEmpty(adid);
    }
    @Nullable
    public String getRuntime() {
        return runtime;
    }
    public void setRuntime(@Nullable String runtime) { this.runtime = PCIFormatter.nullIfEmpty(runtime); }

    @Nullable
    public String getPartnerCode() {
        return partnercode;
    }
    public void setPartnerCode(@Nullable String partnercode) { this.partnercode = PCIFormatter.nullIfEmpty(partnercode); }
    public int getPcimode(){return pcimode;}
    public void setPcimode(@NonNull int pcimode){ this.pcimode = pcimode; }

//    @Nullable
//    public String getPhoneNumber() {
//        return phoneNumber;
//    }
//    public void setPhoneNumber(@Nullable String phoneNumber) {
//        this.phoneNumber = PCIFormatter.maskPhoneNumber(phoneNumber);
//    }


    /* 각종 동의 여부 */
    public boolean isTermAgreed() {
        return isTermAgreed;
    }
    public void setTermAgreed(boolean termAgreed) {
        isTermAgreed = termAgreed;
    }

//    public boolean isAdidUseAgreed() {
//        return isAdidUseAgreed;
//    }
//    public void setAdidUseAgreed(boolean adidUseAgreed) {
//        isAdidUseAgreed = adidUseAgreed;
//    }

//    public boolean isAdPushAgreed() {
//        return isAdPushAgreed;
//    }
//    public void setAdPushAgreed(boolean adPushAgreed) {
//        isAdPushAgreed = adPushAgreed;
//    }
//    public boolean isBleUseAgreed() {
//        return isBleUseAgreed;
//    }
//    public void setBleUseAgreed(boolean bleUseAgreed) {
//        isBleUseAgreed = bleUseAgreed;
//    }
    public boolean isOptIn() {
    return isOptIn;
}
    public void setOptIn(boolean optIn) {
        isOptIn = optIn;
    }


    /* Type */
    @Override
    @NonNull
    public final Type getType() {
        return type;
    }
    public enum Type {
        DEFAULT(1),
        INACTIVE(2),
        ACTIVE(3);
        private int value;
        Type(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }


    /* other overrides */
    @Override
    public void onKeep() {
        // Todo : override this
    }

    @Override
    public void onEnter(@NonNull PCIState.Type prevStateLevel) {
        // Todo : override this
    }

    @Override
    public void onLeave(@NonNull PCIState.Type nextStateLevel) {
        // Todo : override this
    }
}
