package com.fivesys.alphamanufacturas.fivesys.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;


public class Util {

    private static final String FolderImg = "FiveSYS";
    public static final String Error = "Por favor volver a reintentar !";

    private static String FechaActual;
    private static Date date;

    private static final int img_height_default = 800;
    private static final int img_width_default = 600;


    public static String getFechaActual() {
        date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        FechaActual = format.format(date);
        return FechaActual;
    }

    public static String getHoraActual() {
        date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss aaa");
        FechaActual = format.format(date);
        return FechaActual;
    }

    public static String getFechaEditar() {
        date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        FechaActual = format.format(date);
        return FechaActual;
    }

    public static String getFechaActualForPhoto(int id) {
        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy_HHmmssSSSS");
        FechaActual = format.format(date);
        return id + "_" + FechaActual;
    }

    public static String getFechaActualRepartoPhoto(int id, String codigo) {
        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy_HHmmssSSSS");
        FechaActual = format.format(date);
        return id + "_" + codigo + "_" + FechaActual;
    }

    public static void toggleTextInputLayoutError(@NonNull TextInputLayout textInputLayout,
                                                  String msg) {
        textInputLayout.setError(msg);
        if (msg == null) {
            textInputLayout.setErrorEnabled(false);
        } else {
            textInputLayout.setErrorEnabled(true);
        }
    }


    // TODO SOBRE ADJUNTAR PHOTO

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }
        FileChannel source;
        FileChannel destination;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        destination.close();
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String result = null;
        String[] proj = {MediaStore.Video.Media.DATA};
        @SuppressLint("Recycle") Cursor cursor = Objects.requireNonNull(context).getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }


    public static File getFolder() {
        File folder = new File(Environment.getExternalStorageDirectory(), FolderImg);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }

    public static File getFolderPhoto() {
        File folder = new File(Environment.getExternalStorageDirectory(), FolderImg);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }

    // TODO SOBRE FOTO
    public static boolean comprimirImagen(String PathFile) {
        try {
            String result = getRightAngleImage(PathFile);
            return result.equals(PathFile);
        } catch (Exception ex) {
            Log.i("exception", ex.getMessage());
            return false;
        }
    }


    private static String getDateTimeFormatString(Date date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy - hh:mm:ss a");
        return df.format(date);
    }


    private static Bitmap ProcessingBitmap_SetDATETIME(Bitmap bm1, String captionString) {
        //Bitmap bm1 = null;
        Bitmap newBitmap = null;
        try {

            Bitmap.Config config = bm1.getConfig();
            if (config == null) {
                config = Bitmap.Config.ARGB_8888;
            }
            newBitmap = Bitmap.createBitmap(bm1.getWidth(), bm1.getHeight(), config);

            Canvas newCanvas = new Canvas(newBitmap);
            newCanvas.drawBitmap(bm1, 0, 0, null);

            if (captionString != null) {

                Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
                paintText.setColor(Color.RED);
                paintText.setTextSize(22);
                paintText.setStyle(Paint.Style.FILL);
                paintText.setShadowLayer(0.7f, 0.7f, 0.7f, Color.YELLOW);

                Rect rectText = new Rect();
                paintText.getTextBounds(captionString, 0, captionString.length(), rectText);
                newCanvas.drawText(captionString, 0, rectText.height(), paintText);
            }

            //} catch (FileNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newBitmap;
    }


    private static String CopyBitmatToFile(String filename, Bitmap bitmap) {
        try {
            File f = new File(filename);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, bos);
            byte[] bitmapdata = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            return "true";

        } catch (IOException ex) {
            return ex.getMessage();
        }
    }


    private static Bitmap ShrinkBitmap(String file, int width, int height) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        options.inJustDecodeBounds = true;

        int heightRatio = (int) Math.ceil(options.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(options.outWidth / (float) width);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                options.inSampleSize = heightRatio;
            } else {
                options.inSampleSize = widthRatio;
            }
        }

        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(file, options);

    }


    private static void ShrinkBitmapOnlyReduce(String file, int width, int height, String captionString) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        options.inJustDecodeBounds = true;

        int heightRatio = (int) Math.ceil(options.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(options.outWidth / (float) width);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                options.inSampleSize = heightRatio;
            } else {
                options.inSampleSize = widthRatio;
            }
        }

        options.inJustDecodeBounds = false;

        try {


            Bitmap b = BitmapFactory.decodeFile(file, options);

            Bitmap.Config config = b.getConfig();
            if (config == null) {
                config = Bitmap.Config.ARGB_8888;
            }
            Bitmap newBitmap = Bitmap.createBitmap(b.getWidth(), b.getHeight(), config);

            Canvas newCanvas = new Canvas(newBitmap);
            newCanvas.drawBitmap(b, 0, 0, null);

            if (captionString != null) {

                Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
                paintText.setColor(Color.RED);
                paintText.setTextSize(22);
                paintText.setStyle(Paint.Style.FILL);
                paintText.setShadowLayer(0.7f, 0.7f, 0.7f, Color.YELLOW);

                Rect rectText = new Rect();
                paintText.getTextBounds(captionString, 0, captionString.length(), rectText);
                newCanvas.drawText(captionString, 0, rectText.height(), paintText);
            }

            FileOutputStream fOut = new FileOutputStream(file);
            String imageName = file.substring(file.lastIndexOf("/") + 1);
            String imageType = imageName.substring(imageName.lastIndexOf(".") + 1);

            FileOutputStream out = new FileOutputStream(file);
            if (imageType.equalsIgnoreCase("png")) {
                newBitmap.compress(Bitmap.CompressFormat.PNG, 70, out);
            } else if (imageType.equalsIgnoreCase("jpeg") || imageType.equalsIgnoreCase("jpg")) {
                newBitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
            }
            fOut.flush();
            fOut.close();
            newBitmap.recycle();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // TODO SOBRE ROTAR LA PHOTO

    private static String getRightAngleImage(String photoPath) {

        try {
            ExifInterface ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int degree;

            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    degree = 0;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                case ExifInterface.ORIENTATION_UNDEFINED:
                    degree = 0;
                    break;
                default:
                    degree = 90;
            }

            return rotateImage(degree, photoPath);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return photoPath;
    }

    private static String rotateImage(int degree, String imagePath) {

        if (degree <= 0) {
            ShrinkBitmapOnlyReduce(imagePath, img_width_default, img_height_default, getDateTimeFormatString(new Date(new File(imagePath).lastModified())));
            return imagePath;
        }
        try {

            Bitmap b = ShrinkBitmap(imagePath, img_width_default, img_height_default);
            Matrix matrix = new Matrix();
            if (b.getWidth() > b.getHeight()) {
                matrix.setRotate(degree);
                b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
                b = ProcessingBitmap_SetDATETIME(b, getDateTimeFormatString(new Date(new File(imagePath).lastModified())));
            }

            FileOutputStream fOut = new FileOutputStream(imagePath);
            String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            String imageType = imageName.substring(imageName.lastIndexOf(".") + 1);

            FileOutputStream out = new FileOutputStream(imagePath);
            if (imageType.equalsIgnoreCase("png")) {
                b.compress(Bitmap.CompressFormat.PNG, 70, out);
            } else if (imageType.equalsIgnoreCase("jpeg") || imageType.equalsIgnoreCase("jpg")) {
                b.compress(Bitmap.CompressFormat.JPEG, 70, out);
            }
            fOut.flush();
            fOut.close();
            b.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagePath;
    }

    // TODO SOBRE MICROFONO

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

}


