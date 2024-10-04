/**
 * A boolean URL query parameter value used to specify the review session failure landing page.
 */
const rsErrorParam = "review-error";
/**
 * A boolean URL query parameter value used to specify the review session completion landing page.
 */
const rsCompletedParam = "review-complete";
/**
 * A boolean URL query parameter for account logout.
 */
const logoutParam = "logout";
/**
 * A boolean URL query parameter for account deletion feedback.
 */
const accountDelParam = "accountDeleted";
/**
 * A generic error boolean URL query parameter.
 */
const errorParam = "error";

/**
 * Session storage key for currently fetched review cards.
 */
const RC_SKEY = "reviewCards";
/**
 * Session storage key for the number of remaining cards to review in the current deck.
 */
const RR_SKEY = "remainingReviews";
/**
 * Session storage key for specifying whether an account is currently authenticated or not.
 * @type {boolean}
 */
const LOGGED_IN_SKEY = "loggedIn";


export { rsErrorParam, rsCompletedParam, RC_SKEY, RR_SKEY, LOGGED_IN_SKEY, logoutParam, accountDelParam, errorParam };
