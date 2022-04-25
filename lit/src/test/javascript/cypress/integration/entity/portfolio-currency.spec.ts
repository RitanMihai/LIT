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

describe('PortfolioCurrency e2e test', () => {
  const portfolioCurrencyPageUrl = '/portfolio-currency';
  const portfolioCurrencyPageUrlPattern = new RegExp('/portfolio-currency(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const portfolioCurrencySample = { code: 'Sleek' };

  let portfolioCurrency: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/services/portfolio/api/portfolio-currencies+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/portfolio/api/portfolio-currencies').as('postEntityRequest');
    cy.intercept('DELETE', '/services/portfolio/api/portfolio-currencies/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (portfolioCurrency) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/portfolio/api/portfolio-currencies/${portfolioCurrency.id}`,
      }).then(() => {
        portfolioCurrency = undefined;
      });
    }
  });

  it('PortfolioCurrencies menu should load PortfolioCurrencies page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('portfolio-currency');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('PortfolioCurrency').should('exist');
    cy.url().should('match', portfolioCurrencyPageUrlPattern);
  });

  describe('PortfolioCurrency page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(portfolioCurrencyPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create PortfolioCurrency page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/portfolio-currency/new$'));
        cy.getEntityCreateUpdateHeading('PortfolioCurrency');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', portfolioCurrencyPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/portfolio/api/portfolio-currencies',
          body: portfolioCurrencySample,
        }).then(({ body }) => {
          portfolioCurrency = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/portfolio/api/portfolio-currencies+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [portfolioCurrency],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(portfolioCurrencyPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details PortfolioCurrency page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('portfolioCurrency');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', portfolioCurrencyPageUrlPattern);
      });

      it('edit button click should load edit PortfolioCurrency page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PortfolioCurrency');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', portfolioCurrencyPageUrlPattern);
      });

      it('last delete button click should delete instance of PortfolioCurrency', () => {
        cy.intercept('GET', '/services/portfolio/api/portfolio-currencies/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('portfolioCurrency').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', portfolioCurrencyPageUrlPattern);

        portfolioCurrency = undefined;
      });
    });
  });

  describe('new PortfolioCurrency page', () => {
    beforeEach(() => {
      cy.visit(`${portfolioCurrencyPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('PortfolioCurrency');
    });

    it('should create an instance of PortfolioCurrency', () => {
      cy.get(`[data-cy="code"]`).type('Handmade').should('have.value', 'Handmade');

      cy.get(`[data-cy="name"]`).type('Associate').should('have.value', 'Associate');

      cy.get(`[data-cy="currencySymbol"]`).type('CHF').should('have.value', 'CHF');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        portfolioCurrency = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', portfolioCurrencyPageUrlPattern);
    });
  });
});
