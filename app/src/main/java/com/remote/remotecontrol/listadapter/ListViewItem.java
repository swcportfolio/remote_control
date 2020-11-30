package com.remote.remotecontrol.listadapter;

import android.graphics.drawable.Drawable;

/**
 *  ListView Item 클래스
 * @author 박상원
 * @since @since 2020. 11. 28
 * @version 1.1
 * @see

 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *      수정일		   수정자             수정내용
 *  =============   ==========      ==============================
 *  2020. 11. 28     박상원              최초 생성
 *
 *  ==============================================================
 * </pre>
 */
public class ListViewItem {
    private String brandName;

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }
}
