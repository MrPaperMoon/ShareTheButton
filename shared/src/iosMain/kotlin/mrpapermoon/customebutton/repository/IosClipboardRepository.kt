package mrpapermoon.customebutton.repository

import platform.UIKit.UIPasteboard

/** Writes invitation links to the iOS general pasteboard. */
class IosClipboardRepository : ClipboardRepository {
    override fun copy(text: String) {
        UIPasteboard.generalPasteboard.string = text
    }
}
