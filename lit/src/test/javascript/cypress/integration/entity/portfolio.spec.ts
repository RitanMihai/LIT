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

describe('Portfolio e2e test', () => {
  const portfolioPageUrl = '/portfolio';
  const portfolioPageUrlPattern = new RegExp('/portfolio(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const portfolioSample = { name: 'SQL leverage' };

  let portfolio: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/services/portfolio/api/portfolios+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/portfolio/api/portfolios').as('postEntityRequest');
    cy.intercept('DELETE', '/services/portfolio/api/portfolios/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (portfolio) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/portfolio/api/portfolios/${portfolio.id}`,
      }).then(() => {
        portfolio = undefined;
      });
    }
  });

  it('Portfolios menu should load Portfolios page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('portfolio');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Portfolio').should('exist');
    cy.url().should('match', portfolioPageUrlPattern);
  });

  describe('Portfolio page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(portfolioPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Portfolio page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/portfolio/new$'));
        cy.getEntityCreateUpdateHeading('Portfolio');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', portfolioPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/portfolio/api/portfolios',
          body: portfolioSample,
        }).then(({ body }) => {
          portfolio = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/portfolio/api/portfolios+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/services/portfolio/api/portfolios?page=0&size=20>; rel="last",<http://localhost/services/portfolio/api/portfolios?page=0&size=20>; rel="first"',
              },
              body: [portfolio],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(portfolioPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Portfolio page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('portfolio');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', portfolioPageUrlPattern);
      });

      it('edit button click should load edit Portfolio page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Portfolio');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', portfolioPageUrlPattern);
      });

      it('last delete button click should delete instance of Portfolio', () => {
        cy.intercept('GET', '/services/portfolio/api/portfolios/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('portfolio').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', portfolioPageUrlPattern);

        portfolio = undefined;
      });
    });
  });

  describe('new Portfolio page', () => {
    beforeEach(() => {
      cy.visit(`${portfolioPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Portfolio');
    });

    it('should create an instance of Portfolio', () => {
      cy.get(`[data-cy="name"]`).type('Frozen').should('have.value', 'Frozen');

      cy.get(`[data-cy="value"]`).type('94011').should('have.value', '94011');

      cy.setFieldImageAsBytesOfEntity('image', 'integration-test.png', 'image/png');

      cy.get(`[data-cy="unrealisedValue"]`).type('3414').should('have.value', '3414');

      cy.get(`[data-cy="profitOrLoss"]`).type('33077').should('have.value', '33077');

      // since cypress clicks submit too fast before the blob fields are validated
      cy.wait(200); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        portfolio = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', portfolioPageUrlPattern);
    });
  });
});
