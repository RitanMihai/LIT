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

describe('SocialUser e2e test', () => {
  const socialUserPageUrl = '/social-user';
  const socialUserPageUrlPattern = new RegExp('/social-user(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const socialUserSample = { user: 46804 };

  let socialUser: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/services/social/api/social-users+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/social/api/social-users').as('postEntityRequest');
    cy.intercept('DELETE', '/services/social/api/social-users/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (socialUser) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/social/api/social-users/${socialUser.id}`,
      }).then(() => {
        socialUser = undefined;
      });
    }
  });

  it('SocialUsers menu should load SocialUsers page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('social-user');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('SocialUser').should('exist');
    cy.url().should('match', socialUserPageUrlPattern);
  });

  describe('SocialUser page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(socialUserPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create SocialUser page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/social-user/new$'));
        cy.getEntityCreateUpdateHeading('SocialUser');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', socialUserPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/social/api/social-users',
          body: socialUserSample,
        }).then(({ body }) => {
          socialUser = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/social/api/social-users+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [socialUser],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(socialUserPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details SocialUser page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('socialUser');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', socialUserPageUrlPattern);
      });

      it('edit button click should load edit SocialUser page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SocialUser');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', socialUserPageUrlPattern);
      });

      it('last delete button click should delete instance of SocialUser', () => {
        cy.intercept('GET', '/services/social/api/social-users/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('socialUser').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', socialUserPageUrlPattern);

        socialUser = undefined;
      });
    });
  });

  describe('new SocialUser page', () => {
    beforeEach(() => {
      cy.visit(`${socialUserPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('SocialUser');
    });

    it('should create an instance of SocialUser', () => {
      cy.get(`[data-cy="user"]`).type('47923').should('have.value', '47923');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        socialUser = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', socialUserPageUrlPattern);
    });
  });
});
