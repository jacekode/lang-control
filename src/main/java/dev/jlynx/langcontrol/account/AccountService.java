package dev.jlynx.langcontrol.account;

import dev.jlynx.langcontrol.account.dto.*;
import dev.jlynx.langcontrol.auth.dto.RegisterRequestBody;
import dev.jlynx.langcontrol.exception.WrongPasswordException;
import dev.jlynx.langcontrol.exception.UsernameAlreadyExistsException;
import dev.jlynx.langcontrol.exception.ValuesTheSameException;

public interface AccountService {

    /**
     * Creates a new Account and persists it in the database.
     *
     * @param body the request body object; usually received from a controller
     * @return an overview summary object of the newly registered account
     * @throws UsernameAlreadyExistsException if the chosen {@code username} is already used by another account
     */
    AccountOverviewResponse registerAccount(RegisterRequestBody body);

    /**
     * Updates the {@code username} field of the currently authenticated account.
     *
     * @param body the request body object; usually received from a controller
     * @return an overview summary object of the updated account
     * @throws ValuesTheSameException if the new {@code username} value doesn't differ from the current one
     * @throws UsernameAlreadyExistsException if the {@code username} is already used by another account
     */
    AccountOverviewResponse updateAccount(UpdateAccountAndUserProfileRequest body);

    /**
     * Updates the currently logged in account's password with a new valid value. Prior to the update, it checks
     * the correctness of the submitted current password value.
     *
     * @param body the request body object; usually received from a controller
     * @return an overview summary object of the updated account
     * @throws WrongPasswordException if the provided values for the current password is incorrect
     */
    AccountOverviewResponse changePassword(UpdatePasswordRequest body);

    /**
     * Removes the current account entity and related entities from the database.
     *
     * @param body the request body object; usually received from a controller
     * @throws WrongPasswordException if the submitted password is incorrect
     */
    void deleteAccount(DeleteAccountRequest body);

    /**
     * Retrieves the {@link Account} entity object of the currently authenticated user.
     *
     * @return the currently authenticated user's {@link Account} object
     */
    Account retrieveCurrentAccountEntity();

    /**
     * Returns the currently logged in Account's overview DTO object.
     * @return Currently logged in Account's transfer data object.
     */
    AccountOverviewResponse getCurrentAccount();
}
