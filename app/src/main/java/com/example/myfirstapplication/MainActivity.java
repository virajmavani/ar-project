package com.example.myfirstapplication;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import uk.co.appoly.arcorelocation.LocationScene;

public class MainActivity extends AppCompatActivity {

    private ArFragment arFragment;
    private LocationScene locationScene;
    private ModelRenderable modelRenderable = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_ux_fragment);

        ModelRenderable.builder()
                //get the context of the ARFragment and pass the name of your .sfb file
                .setSource(getBaseContext(), Uri.parse("model.sfb"))
                .build()  .thenAcceptAsync{ modelRenderable -> this@MainActivity.modelRenderable = modelRenderable }

                //I accepted the CompletableFuture using Async since I created my model on creation of the activity. You could simply use .thenAccept too.
                //Use the returned modelRenderable and save it to a global variable of the same name
            //    .thenAcceptAsync { modelRenderable -> this@MainActivity.modelRenderable = modelRenderable }
//        modelRenderable -> this@MainActivity.modelRenderable = modelRenderable

//        ArSceneView scene = arFragment.getArSceneView();
//        Session mSession = null;
//        try {
//             mSession = new Session(this);//scene.getSession();
//        } catch (UnavailableArcoreNotInstalledException e) {
//            e.printStackTrace();
//        } catch (UnavailableApkTooOldException e) {
//            e.printStackTrace();
//        } catch (UnavailableSdkTooOldException e) {
//            e.printStackTrace();
//        } catch (UnavailableDeviceNotCompatibleException e) {
//            e.printStackTrace();
//        }
//
//        Anchor newAnchor = null;
//        for (Plane plane : mSession.getAllTrackables(Plane.class)) {
//            if (plane.getType() == Plane.Type.HORIZONTAL_UPWARD_FACING
//                    && plane.getTrackingState() == TrackingState.TRACKING)
//            {
//                newAnchor = plane.createAnchor(plane.getCenterPose());
//                break;
//            }
//        }
//
//        Anchor finalNewAnchor = newAnchor;
//        ModelRenderable.builder()
//                .setSource(this, Uri.parse("model.sfb"))
//                .build()
//                .thenAccept(modelRenderable -> addModelToScene(finalNewAnchor, modelRenderable))
//                .exceptionally(throwable -> {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                    builder.setMessage(throwable.getMessage())
//                            .show();
//                    return null;
//                });

        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {

            Anchor anchor = hitResult.createAnchor();
            Log.d("TAG", "onCreate: "+anchor);

            ModelRenderable.builder()
                    .setSource(this, Uri.parse("model.sfb"))
                    .build()
                    .thenAccept(modelRenderable -> addModelToScene(anchor, modelRenderable))
                    .exceptionally(throwable -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(throwable.getMessage())
                                .show();
                        return null;
                    });
        });
    }

    private void addModelToScene(Anchor anchor, ModelRenderable modelRenderable) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
        transformableNode.setParent(anchorNode);
        transformableNode.setRenderable(modelRenderable);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        transformableNode.select();
    }
}
