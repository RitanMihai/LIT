import { entityItemSelector } from '../../support/commands';
import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('UserFollowing e2e test', () => {
  const userFollowingPageUrl = '/user-following';
  const userFollowingPageUrlPattern = new RegExp('/user-following(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const userFollowingSample = { stock: 38798 };

  let userFollowing: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/services/social/api/user-followings+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/social/api/user-followings').as('postEntityRequest');
    cy.intercept('DELETE', '/services/social/api/user-followings/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (userFollowing) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/social/api/user-followings/${userFollowing.id}`,
      }).then(() => {
        userFollowing = undefined;
      });
    }
  });

  it('UserFollowings menu should load UserFollowings page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('user-following');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('UserFollowing').should('exist');
    cy.url().should('match', userFollowingPageUrlPattern);
  });

  describe('UserFollowing page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(userFollowingPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create UserFollowing page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/user-following/new$'));
        cy.getEntityCreateUpdateHeading('UserFollowing');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', userFollowingPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/social/api/user-followings',
          body: userFollowingSample,
        }).then(({ body }) => {
          userFollowing = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/social/api/user-followings+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [userFollowing],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(userFollowingPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details UserFollowing page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('userFollowing');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', userFollowingPageUrlPattern);
      });

      it('edit button click should load edit UserFollowing page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('UserFollowing');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', userFollowingPageUrlPattern);
      });

      it('last delete button click should delete instance of UserFollowing', () => {
        cy.intercept('GET', '/services/social/api/user-followings/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('userFollowing').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', userFollowingPageUrlPattern);

        userFollowing = undefined;
      });
    });
  });

  describe('new UserFollowing page', () => {
    beforeEach(() => {
      cy.visit(`${userFollowingPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('UserFollowing');
    });

    it('should create an instance of UserFollowing', () => {
      cy.get(`[data-cy="stock"]`).type('57025').should('have.value', '57025');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        userFollowing = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', userFollowingPageUrlPattern);
    });
  });
});
