package org.kvxd.fasterblockentities.datagen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quadrant;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.client.resources.model.cuboid.CuboidFace;
import net.minecraft.core.Direction;
import org.joml.Matrix4fc;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class CuboidExtractor {

    public record Face(float minU, float minV, float maxU, float maxV, int rotation) {
    }

    public record Element(Vector3f from, Vector3f to, Map<Direction, Face> faces) {
    }

    private static final float EPSILON = 1.0E-3F;
    private static final float PRECISION = 100000.0F;

    public static List<Element> extract(final LayerDefinition layer, final Matrix4fc transform) {
        List<Element> elements = new ArrayList<>();
        PoseStack poseStack = new PoseStack();
        poseStack.mulPose(transform);
        layer.bakeRoot().visit(poseStack, (pose, path, index, cube) -> elements.add(element(pose.pose(), cube)));
        return elements;
    }

    private static Element element(final Matrix4fc matrix, final ModelPart.Cube cube) {
        if (cube.polygons.length != Direction.values().length) {
            throw new IllegalStateException("Cube has " + cube.polygons.length + " faces, so its bounds cannot be recovered from its corners");
        }

        List<float[][]> quads = new ArrayList<>();
        for (ModelPart.Polygon polygon : cube.polygons) {
            float[][] quad = new float[4][];
            for (int i = 0; i < 4; i++) {
                ModelPart.Vertex vertex = polygon.vertices()[i];
                Vector3f position = matrix.transformPosition(vertex.worldX(), vertex.worldY(), vertex.worldZ(), new Vector3f());
                quad[i] = new float[]{
                    round(position.x() * 16.0F),
                    round(position.y() * 16.0F),
                    round(position.z() * 16.0F),
                    round(vertex.u() * 16.0F),
                    round(vertex.v() * 16.0F)
                };
            }
            quads.add(quad);
        }

        Vector3f from = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
        Vector3f to = new Vector3f(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);
        for (float[][] quad : quads) {
            for (float[] vertex : quad) {
                from.set(Math.min(from.x, vertex[0]), Math.min(from.y, vertex[1]), Math.min(from.z, vertex[2]));
                to.set(Math.max(to.x, vertex[0]), Math.max(to.y, vertex[1]), Math.max(to.z, vertex[2]));
            }
        }

        Map<Direction, Face> faces = new EnumMap<>(Direction.class);
        for (int i = 0; i < quads.size(); i++) {
            Direction direction = direction(matrix, cube.polygons[i]);
            Face previous = faces.put(direction, face(quads.get(i), direction, from, to));
            if (previous != null) {
                throw new IllegalStateException("Two quads resolved to the same face " + direction);
            }
        }
        return new Element(from, to, faces);
    }

    private static Direction direction(final Matrix4fc matrix, final ModelPart.Polygon polygon) {
        Vector3f normal = matrix.transformDirection(new Vector3f(polygon.normal())).normalize();
        Direction closest = null;
        float bestDot = 0.0F;
        for (Direction candidate : Direction.values()) {
            float dot = normal.dot(candidate.getUnitVec3f());
            if (dot > bestDot) {
                bestDot = dot;
                closest = candidate;
            }
        }
        if (closest == null) {
            throw new IllegalStateException("Face normal " + normal + " does not point at any block face");
        }
        return closest;
    }

    private static Face face(final float[][] quad, final Direction direction, final Vector3f from, final Vector3f to) {
        FaceInfo info = FaceInfo.fromFacing(direction);
        float[][] uvs = new float[4][];
        for (int i = 0; i < 4; i++) {
            Vector3f corner = info.getVertexInfo(i).select(from, to);
            for (float[] vertex : quad) {
                if (near(vertex[0], corner.x) && near(vertex[1], corner.y) && near(vertex[2], corner.z)) {
                    uvs[i] = new float[]{vertex[3], vertex[4]};
                    break;
                }
            }
            if (uvs[i] == null) {
                throw new IllegalStateException("No vertex of the " + direction + " quad sits at corner " + corner);
            }
        }
        Face face = solve(uvs, direction);
        verify(face, uvs, direction);
        return face;
    }

    private static void verify(final Face face, final float[][] uvs, final Direction direction) {
        CuboidFace.UVs rect = new CuboidFace.UVs(face.minU(), face.minV(), face.maxU(), face.maxV());
        Quadrant quadrant = Quadrant.values()[face.rotation() / 90];
        for (int i = 0; i < 4; i++) {
            float u = CuboidFace.getU(rect, quadrant, i) * 16.0F;
            float v = CuboidFace.getV(rect, quadrant, i) * 16.0F;
            if (!near(u, uvs[i][0]) || !near(v, uvs[i][1])) {
                throw new IllegalStateException(
                    "Baking the generated " + direction + " face gives uv " + u + "," + v
                        + " at vertex " + i + " but the entity model has " + uvs[i][0] + "," + uvs[i][1]
                );
            }
        }
    }

    private static Face solve(final float[][] uvs, final Direction direction) {
        for (int shift = 0; shift < 4; shift++) {
            Float minU = null;
            Float maxU = null;
            Float minV = null;
            Float maxV = null;
            boolean fits = true;
            for (int i = 0; i < 4 && fits; i++) {
                int rotated = (i + shift) % 4;
                float u = uvs[i][0];
                float v = uvs[i][1];
                if (rotated == 0 || rotated == 1) {
                    if (minU == null) {
                        minU = u;
                    } else {
                        fits &= near(minU, u);
                    }
                } else if (maxU == null) {
                    maxU = u;
                } else {
                    fits &= near(maxU, u);
                }
                if (rotated == 0 || rotated == 3) {
                    if (minV == null) {
                        minV = v;
                    } else {
                        fits &= near(minV, v);
                    }
                } else if (maxV == null) {
                    maxV = v;
                } else {
                    fits &= near(maxV, v);
                }
            }
            if (fits) {
                return new Face(minU, minV, maxU, maxV, shift * 90);
            }
        }
        throw new IllegalStateException("No uv rotation maps the " + direction + " quad onto a block model face");
    }

    private static boolean near(final float a, final float b) {
        return Math.abs(a - b) < EPSILON;
    }

    private static float round(final float value) {
        return Math.round(value * PRECISION) / PRECISION;
    }

    private CuboidExtractor() {
    }
}
