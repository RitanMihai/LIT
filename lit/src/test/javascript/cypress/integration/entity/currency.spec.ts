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

describe('Currency e2e test', () => {
  const currencyPageUrl = '/currency';
  const currencyPageUrlPattern = new RegExp('/currency(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const currencySample = { code: 'Kuna Buckinghamshire' };

  let currency: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/services/watcher/api/currencies+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/watcher/api/currencies').as('postEntityRequest');
    cy.intercept('DELETE', '/services/watcher/api/currencies/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (currency) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/watcher/api/currencies/${currency.id}`,
      }).then(() => {
        currency = undefined;
      });
    }
  });

  it('Currencies menu should load Currencies page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('currency');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Currency').should('exist');
    cy.url().should('match', currencyPageUrlPattern);
  });

  describe('Currency page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(currencyPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Currency page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/currency/new$'));
        cy.getEntityCreateUpdateHeading('Currency');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', currencyPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/watcher/api/currencies',
          body: currencySample,
        }).then(({ body }) => {
          currency = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/watcher/api/currencies+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [currency],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(currencyPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Currency page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('currency');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', currencyPageUrlPattern);
      });

      it('edit button click should load edit Currency page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Currency');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', currencyPageUrlPattern);
      });

      it('last delete button click should delete instance of Currency', () => {
        cy.intercept('GET', '/services/watcher/api/currencies/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('currency').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', currencyPageUrlPattern);

        currency = undefined;
      });
    });
  });

  describe('new Currency page', () => {
    beforeEach(() => {
      cy.visit(`${currencyPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Currency');
    });

    it('should create an instance of Currency', () => {
      cy.get(`[data-cy="code"]`).type('maroon calculate').should('have.value', 'maroon calculate');

      cy.get(`[data-cy="name"]`).type('composite synthesize').should('have.value', 'composite synthesize');

      cy.get(`[data-cy="currencySymbol"]`).type('₺').should('have.value', '₺');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        currency = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', currencyPageUrlPattern);
    });
  });
});
