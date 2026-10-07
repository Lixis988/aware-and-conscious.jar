package grondag.darkness;

public interface LightmapAccess {
   boolean darkness_isDirty();
   float darkness_prevFlicker();
   void setDirty(boolean dirty);
}
