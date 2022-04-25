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

describe('UserReaction e2e test', () => {
  const userReactionPageUrl = '/user-reaction';
  const userReactionPageUrlPattern = new RegExp('/user-reaction(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const userReactionSample = { type: 'LOVE' };

  let userReaction: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/services/social/api/user-reactions+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/social/api/user-reactions').as('postEntityRequest');
    cy.intercept('DELETE', '/services/social/api/user-reactions/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (userReaction) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/social/api/user-reactions/${userReaction.id}`,
      }).then(() => {
        userReaction = undefined;
      });
    }
  });

  it('UserReactions menu should load UserReactions page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('user-reaction');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('UserReaction').should('exist');
    cy.url().should('match', userReactionPageUrlPattern);
  });

  describe('UserReaction page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(userReactionPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create UserReaction page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/user-reaction/new$'));
        cy.getEntityCreateUpdateHeading('UserReaction');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', userReactionPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/social/api/user-reactions',
          body: userReactionSample,
        }).then(({ body }) => {
          userReaction = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/social/api/user-reactions+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/services/social/api/user-reactions?page=0&size=20>; rel="last",<http://localhost/services/social/api/user-reactions?page=0&size=20>; rel="first"',
              },
              body: [userReaction],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(userReactionPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details UserReaction page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('userReaction');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', userReactionPageUrlPattern);
      });

      it('edit button click should load edit UserReaction page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('UserReaction');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', userReactionPageUrlPattern);
      });

      it('last delete button click should delete instance of UserReaction', () => {
        cy.intercept('GET', '/services/social/api/user-reactions/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('userReaction').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', userReactionPageUrlPattern);

        userReaction = undefined;
      });
    });
  });

  describe('new UserReaction page', () => {
    beforeEach(() => {
      cy.visit(`${userReactionPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('UserReaction');
    });

    it('should create an instance of UserReaction', () => {
      cy.get(`[data-cy="type"]`).select('AMUSING');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        userReaction = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', userReactionPageUrlPattern);
    });
  });
});
