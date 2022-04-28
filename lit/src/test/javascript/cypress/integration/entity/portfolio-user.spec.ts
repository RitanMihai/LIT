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

describe('PortfolioUser e2e test', () => {
  const portfolioUserPageUrl = '/portfolio-user';
  const portfolioUserPageUrlPattern = new RegExp('/portfolio-user(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const portfolioUserSample = { user: 'asynchronous HDD AI' };

  let portfolioUser: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/services/portfolio/api/portfolio-users+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/portfolio/api/portfolio-users').as('postEntityRequest');
    cy.intercept('DELETE', '/services/portfolio/api/portfolio-users/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (portfolioUser) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/portfolio/api/portfolio-users/${portfolioUser.id}`,
      }).then(() => {
        portfolioUser = undefined;
      });
    }
  });

  it('PortfolioUsers menu should load PortfolioUsers page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('portfolio-user');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('PortfolioUser').should('exist');
    cy.url().should('match', portfolioUserPageUrlPattern);
  });

  describe('PortfolioUser page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(portfolioUserPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create PortfolioUser page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/portfolio-user/new$'));
        cy.getEntityCreateUpdateHeading('PortfolioUser');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', portfolioUserPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/portfolio/api/portfolio-users',
          body: portfolioUserSample,
        }).then(({ body }) => {
          portfolioUser = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/portfolio/api/portfolio-users+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [portfolioUser],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(portfolioUserPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details PortfolioUser page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('portfolioUser');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', portfolioUserPageUrlPattern);
      });

      it('edit button click should load edit PortfolioUser page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PortfolioUser');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', portfolioUserPageUrlPattern);
      });

      it('last delete button click should delete instance of PortfolioUser', () => {
        cy.intercept('GET', '/services/portfolio/api/portfolio-users/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('portfolioUser').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', portfolioUserPageUrlPattern);

        portfolioUser = undefined;
      });
    });
  });

  describe('new PortfolioUser page', () => {
    beforeEach(() => {
      cy.visit(`${portfolioUserPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('PortfolioUser');
    });

    it('should create an instance of PortfolioUser', () => {
      cy.get(`[data-cy="user"]`).type('European Dynamic Pants').should('have.value', 'European Dynamic Pants');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        portfolioUser = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', portfolioUserPageUrlPattern);
    });
  });
});
