package darkkronicle.advancedchat.config;

import darkkronicle.advancedchat.AdvancedChatClient;
import darkkronicle.advancedchat.filters.FilteredMessage;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.TranslatableText;

import java.io.IOException;

public class ConfigFilterScreen extends Screen {

    private ConfigFilter configFilter;
    private String filter;
    private ConfigFilter.ReplaceType replaceType;
    private ConfigFilter.NotifyType notifyType;
    private ButtonWidget notifyTypeWidget;
    private TextFieldWidget textFieldWidget;
    private ButtonWidget typeWidget;
    private boolean ignoreCase;
    private CheckboxWidget ignoreCaseBox;
    private boolean active;
    private CheckboxWidget activeBox;
    private ButtonWidget save;
    private ButtonWidget delete;
    private TextFieldWidget nameWidget;
    private String name;
    private boolean unfilteredInLog;
    private CheckboxWidget unfilteredInLogBox;
    private boolean regex;
    private CheckboxWidget regexBox;
    private TextFieldWidget replaceTextBox;
    private String replaceTo;

    public ConfigFilterScreen(ConfigFilter configFilter) {
        super(new TranslatableText("advancedchat.config.filter"));
        this.configFilter = configFilter;
        filter = configFilter.getTrigger();
        replaceType = configFilter.getReplaceType();
        ignoreCase = configFilter.isIgnoreCase();
        active = configFilter.isActive();
        name = configFilter.getName();
        unfilteredInLog = configFilter.isShowUnFilterInLog();
        regex = configFilter.isRegex();
        replaceTo = configFilter.getReplaceTo();
        notifyType = configFilter.getNotifyType();
    }

    public void init() {
        typeWidget = new ButtonWidget(10, 50, 70, 20, replaceType.name(), button -> {
            replaceType = cycleReplaceType(replaceType);
            button.setMessage(replaceType.name());
        });

        notifyTypeWidget = new ButtonWidget(80, 50, 70, 20, notifyType.name(), button -> {
            notifyType = cycleNotifyType(notifyType);
            button.setMessage(notifyType.name());
        });

        textFieldWidget = new TextFieldWidget(this.font, 10, 80, 150, 20, "String...");
        textFieldWidget.setHasBorder(true);
        textFieldWidget.setChangedListener(this::textChange);
        textFieldWidget.setText(filter);
        textFieldWidget.setMaxLength(128);

        nameWidget = new TextFieldWidget(this.font, 10, 10, 70, 20, "Name");
        nameWidget.setHasBorder(true);
        nameWidget.setChangedListener(this::nameChange);
        nameWidget.setText(name);
        nameWidget.setMaxLength(15);

        replaceTextBox = new TextFieldWidget(this.font, 160, 80, 150, 20, "String...");
        replaceTextBox.setHasBorder(true);
        replaceTextBox.setChangedListener(this::replaceChange);
        replaceTextBox.setText(replaceTo);
        replaceTextBox.setMaxLength(128);

        ignoreCaseBox = new CheckboxWidget(10, 110, 20, 20, "Ignore Case", ignoreCase);

        activeBox = new CheckboxWidget(10, 140, 20, 20, "Active", active);

        unfilteredInLogBox = new CheckboxWidget(10, 170, 20, 20, "Show unfiltered in Log", unfilteredInLog);

        regexBox = new CheckboxWidget(150, 170, 20, 20, "Regex", regex);


        save = new ButtonWidget(10, 200, 50, 20, "Save", button -> {
            save();
        });

        delete = new ButtonWidget(70, 200, 50, 20, "Delete", button -> {
            delete();
        });


        addButton(typeWidget);
        addButton(textFieldWidget);
        addButton(ignoreCaseBox);
        addButton(activeBox);
        addButton(save);
        addButton(nameWidget);
        addButton(delete);
        addButton(unfilteredInLogBox);
        addButton(regexBox);
        addButton(replaceTextBox);
        addButton(notifyTypeWidget);
    }

    private void replaceChange(String s) {
        replaceTo = s;
    }

    public ConfigFilter.ReplaceType cycleReplaceType(ConfigFilter.ReplaceType type) {
        if (type == null || type == ConfigFilter.ReplaceType.NONE) {
            type = ConfigFilter.ReplaceType.NONE;
        }
        ConfigFilter.ReplaceType[] set = ConfigFilter.ReplaceType.values();
        int current = 0;
        for (int i = 0; i < set.length; i++) {
            ConfigFilter.ReplaceType res = set[i];
            if (res == type) {
                current = i;
            }
        }
        if (current > set.length - 2) {
            current = -1;
        }
        return set[current + 1];
    }

    public ConfigFilter.NotifyType cycleNotifyType(ConfigFilter.NotifyType type) {
        if (type == null || type == ConfigFilter.NotifyType.NONE) {
            type = ConfigFilter.NotifyType.NONE;
        }
        ConfigFilter.NotifyType[] set = ConfigFilter.NotifyType.values();
        int current = 0;
        for (int i = 0; i < set.length; i++) {
            ConfigFilter.NotifyType res = set[i];
            if (res == type) {
                current = i;
            }
        }
        if (current > set.length - 2) {
            current = -1;
        }
        return set[current + 1];
    }

    public void textChange(String text) {
        filter = text;
    }

    public void nameChange(String text) {
        name = text;
    }

    public void save() {
        configFilter.setActive(activeBox.isChecked());
        configFilter.setIgnoreCase(ignoreCaseBox.isChecked());
        configFilter.setTrigger(filter);
        configFilter.setReplaceType(replaceType);
        configFilter.setName(name);
        configFilter.setShowUnFilterInLog(unfilteredInLogBox.isChecked());
        configFilter.setRegex(regexBox.isChecked());
        configFilter.setReplaceTo(replaceTo);
        configFilter.setNotifyType(notifyType);
        try {
            AdvancedChatClient.configManager.saveConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
        AdvancedChatClient.mainFilter.reloadFilters();
        minecraft.openScreen(new ConfigMainScreen());

    }

    public void delete() {

        AdvancedChatClient.configObject.configFilters.remove(configFilter);
        try {
            AdvancedChatClient.configManager.saveConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
        AdvancedChatClient.mainFilter.reloadFilters();
        minecraft.openScreen(new ConfigMainScreen());
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        if (this.minecraft.world == null) {
            this.renderDirtBackground(0);
        }
        renderBackground();
        drawCenteredString(this.font, getTitle().asFormattedString(), this.width / 2, (this.height - (this.height + 4 - 48)) / 2 - 4, 16777215);
        super.render(mouseX, mouseY, delta);
    }
}
