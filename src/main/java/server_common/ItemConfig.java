package server_common;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import lombok.Getter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ItemConfig {
    private static final Gson gson = new Gson();

    public record Tag(Model itemModel, Model.TagModel tagModel, String key) {
        public ItemRender getItemRender() {
            for(Model.TagModel.ComponentModel component : this.tagModel().getComponents())
                if(Objects.equals(component.getType(), "meta.ItemRender")) return (ItemRender) component.getData();
            return null;
        }

        @Getter public static class ItemRender {
            private String renderType;
            private String path;
            private String autoUpdate;
        }
    }

    @Getter public static class Model {
        private String id;
        private String category;
        private List<TagModel> tags;

        @Getter public static class TagModel {
            private String id;
            private List<ComponentModel> components;

            @Getter public static class ComponentModel {
                private String type;
                private Object data;
            }
        }

        public String getKey() {
            return this.getCategory()+":"+this.getId();
        }

        public String getKey(String tag) {
            return this.getKey()+"#"+tag;
        }
    }

    public static HashMap<String, Tag> getTags(File itemsDirectory) throws IOException {
        HashMap<String, Tag> tagMap = new HashMap<>();
        getItemMap(itemsDirectory).values().forEach(model -> model.getTags().forEach(tag -> {
            String key = model.getKey(tag.getId());
            tagMap.put(key, new Tag(model, tag, key));
        }));
        return tagMap;
    }

    public static HashMap<String, Model> getItemMap(File itemsDirectory) throws IOException {
        HashMap<String, Model> itemMap = new HashMap<>();
        String[] directories = itemsDirectory.list((current, name) -> new File(current, name).isDirectory());
        assert directories != null;
        for(String directory : directories) {
            File configs = new File(itemsDirectory+"/"+directory);
            File[] files = configs.listFiles();
            assert files != null;
            for(File config : files) {
                String json = Files.asCharSource(config, Charsets.UTF_8).read()
                        .replace("%file_name%", config.getName().split("\\.")[0])
                        .replace("%directory%", directory);
                Model model = gson.fromJson(json, Model.class);
                itemMap.put(model.getKey(), model);
            }
        }
        return itemMap;
    }
}
