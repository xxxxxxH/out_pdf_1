package net.basicmodel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weeboos.permissionlib.PermissionRequest
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.tencent.mmkv.MMKV
import com.xiaoqi.pdftools.util.PDFGenerator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_bottom.*
import net.adapter.PDFAdapter
import net.entity.PDFEntity
import net.utils.GlideEngine
import net.utils.LoadingDialog
import net.utils.MMKVUtils
import java.io.File

class MainActivity : AppCompatActivity() {

    var list: ArrayList<LocalMedia>? = null
    var imgList: ArrayList<String>? = null
    val rootPath = Environment.getExternalStorageDirectory().toString()
    var pdfAdapter: PDFAdapter? = null
    var data: ArrayList<PDFEntity>? = null
    var dialog:AlertDialog?=null
    var loadingDialog:LoadingDialog?= null

    val per = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MMKV.initialize(this)
        requestPermission()
    }

    private fun requestPermission() {
        PermissionRequest.getInstance().build(this)
            .requestPermission(object : PermissionRequest.PermissionListener {
                override fun permissionGranted() {
                    initData()
                    initView()
                }

                override fun permissionDenied(permissions: java.util.ArrayList<String>?) {
                    finish()
                }

                override fun permissionNeverAsk(permissions: java.util.ArrayList<String>?) {
                    finish()
                }
            }, per)
    }

    private fun initData() {
        data = MMKVUtils.getAllDatas("keys")
        pdfAdapter = PDFAdapter(R.layout.layout_item, data)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = pdfAdapter
    }

    private fun initView() {
        pdf.setOnClickListener {
            if (list == null) {
                Toast.makeText(this, "no pictures", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            dialog = createNameDlg()
            dialog!!.show()
        }
        img.setOnClickListener {
            openGallery()
        }
    }

    private fun openGallery() {
        PictureSelector.create(this).openGallery(PictureMimeType.ofImage())
            .imageEngine(GlideEngine.createGlideEngine()).forResult(PictureConfig.CHOOSE_REQUEST)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            list = PictureSelector.obtainMultipleResult(data) as ArrayList<LocalMedia>
            if (list!!.size>0){
                pdf.text = "new pdf (${list!!.size} pistures)"
            }
            handleList()
        }
    }

    private fun newPdf(name: String) {
        val fileName = "$name.pdf"
        val result =
            PDFGenerator.generatePdfFromImage(rootPath + File.separator + fileName, imgList)
        if (result) {
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
            MMKVUtils.saveKeys("keys", fileName)
            val entity = PDFEntity(fileName, rootPath + File.separator + fileName)
            MMKV.defaultMMKV()!!.encode(fileName, entity)
            pdfAdapter!!.addData(entity)
        } else {
            Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show()
        }
        closeLoading()
    }

    private fun handleList() {
        imgList = ArrayList()
        for (item in list!!) {
            imgList!!.add(item.path)
        }
    }

    private fun createNameDlg():AlertDialog{
        val d = AlertDialog.Builder(this).create()
        val view = LayoutInflater.from(this).inflate(R.layout.layout_dialog_name,null)
        d.setView(view)
        val editText =  view.findViewById<EditText>(R.id.fileName)
        editText.setText(System.currentTimeMillis().toString())
        view.findViewById<TextView>(R.id.confirm).setOnClickListener {
            dialog!!.dismiss()
            showLoading()
            newPdf(editText.text.toString())

        }
        return d
    }


    private fun showLoading(){
        if (loadingDialog == null){
            loadingDialog = LoadingDialog(this)
        }
        loadingDialog!!.show()
    }

    private fun closeLoading(){
        if (loadingDialog != null){
            if (loadingDialog!!.isShowing){
                loadingDialog!!.dismiss()
            }
        }
    }
}