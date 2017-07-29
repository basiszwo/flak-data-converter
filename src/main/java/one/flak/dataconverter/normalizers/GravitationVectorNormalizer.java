// Licensed under the GNU GENERAL PUBLIC LICENSE Version 3.
// See LICENSE file in the project root for full license information.

package one.flak.dataconverter.normalizers;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class GravitationVectorNormalizer {

    private Vector3D rotatedGravityVector;
    private Rotation rotationMatrix;

    public static final Vector3D zAxis =  new Vector3D(0.0, 0.0, 1.0);

    public GravitationVectorNormalizer(Vector3D gravityVector ) {
        this.rotationMatrix = buildRotationMatrix(gravityVector);
        this.rotatedGravityVector = rotationMatrix.applyTo(gravityVector);
    }

    public Vector3D normalize(Vector3D input) {
        Vector3D rotatedVector = this.rotationMatrix.applyTo(input);
        return rotatedVector.subtract(this.rotatedGravityVector);
    }

    private Rotation buildRotationMatrix(Vector3D gravityVector) {
        return new Rotation(gravityVector.normalize(), zAxis);
    }

}
