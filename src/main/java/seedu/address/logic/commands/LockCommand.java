package seedu.address.logic.commands;

import seedu.address.commons.events.model.ReloadAddressBookEvent;
import seedu.address.commons.events.storage.DataSavingExceptionEvent;
import seedu.address.commons.exceptions.EncryptOrDecryptException;
import seedu.address.security.Security;
import seedu.address.security.SecurityManager;
import seedu.address.logic.commands.exceptions.CommandException;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

/**
 * Locks the address book.
 */
public class LockCommand extends Command {

    public static final String COMMAND_WORD = "lock";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Encrypts all contact with a input password."
            + "Parameters: "
            + "PASSWORD\n"
            + "Example: " + COMMAND_WORD + " mykey";

    public static final String MESSAGE_SUCCESS = "Address book is locked successfully.";
    public static final String MESSAGE_DUPLICATED_LOCK = "Address book is locked already. Cannot lock again.";
    public static final String MESSAGE_EMPTY_ADDRESSBOOK = "Address book is empty. Nothing to encrypt.";
    public static final String MESSAGE_ERROR_STORAGE_ERROR = "Meets errors during locking address book.";
    public static final String MESSAGE_ERROR_LOCK_PASSWORD = "Cannot lock address book using current password.";

    private final String password;

    public LockCommand(String password) {
        this.password = password;
    }

    @Override
    public CommandResult execute() throws CommandException {
        requireNonNull(model);
        if (isAddressBookEmpty()) {
            return new CommandResult(MESSAGE_EMPTY_ADDRESSBOOK);
        }

        Security security = SecurityManager.getInstance(null);
        try{
            if (security.isEncrypted()) {
                return new CommandResult(MESSAGE_DUPLICATED_LOCK);
            }

            security.encryptAddressBook(password);
            security.raise(new ReloadAddressBookEvent());
            return new CommandResult(MESSAGE_SUCCESS);

        } catch (IOException e) {
            security.raise(new DataSavingExceptionEvent(e));
            return new CommandResult(MESSAGE_ERROR_STORAGE_ERROR);
        } catch (EncryptOrDecryptException e) {
            return new CommandResult(MESSAGE_ERROR_LOCK_PASSWORD);
        }
    }

    /**
     * @return true if address book is empty.
     */
    private boolean isAddressBookEmpty() {
        return model.getAddressBook().getPersonList().isEmpty();
    }
}
