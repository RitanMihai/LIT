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

describe('StockInfo e2e test', () => {
  const stockInfoPageUrl = '/stock-info';
  const stockInfoPageUrlPattern = new RegExp('/stock-info(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const stockInfoSample = { ticker: 'Intelligent Thailand' };

  let stockInfo: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/services/portfolio/api/stock-infos+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/portfolio/api/stock-infos').as('postEntityRequest');
    cy.intercept('DELETE', '/services/portfolio/api/stock-infos/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (stockInfo) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/portfolio/api/stock-infos/${stockInfo.id}`,
      }).then(() => {
        stockInfo = undefined;
      });
    }
  });

  it('StockInfos menu should load StockInfos page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('stock-info');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('StockInfo').should('exist');
    cy.url().should('match', stockInfoPageUrlPattern);
  });

  describe('StockInfo page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(stockInfoPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create StockInfo page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/stock-info/new$'));
        cy.getEntityCreateUpdateHeading('StockInfo');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', stockInfoPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/portfolio/api/stock-infos',
          body: stockInfoSample,
        }).then(({ body }) => {
          stockInfo = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/portfolio/api/stock-infos+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [stockInfo],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(stockInfoPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details StockInfo page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('stockInfo');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', stockInfoPageUrlPattern);
      });

      it('edit button click should load edit StockInfo page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('StockInfo');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', stockInfoPageUrlPattern);
      });

      it('last delete button click should delete instance of StockInfo', () => {
        cy.intercept('GET', '/services/portfolio/api/stock-infos/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('stockInfo').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', stockInfoPageUrlPattern);

        stockInfo = undefined;
      });
    });
  });

  describe('new StockInfo page', () => {
    beforeEach(() => {
      cy.visit(`${stockInfoPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('StockInfo');
    });

    it('should create an instance of StockInfo', () => {
      cy.get(`[data-cy="ticker"]`).type('indexing Villages systemic').should('have.value', 'indexing Villages systemic');

      cy.get(`[data-cy="name"]`).type('generation').should('have.value', 'generation');

      cy.setFieldImageAsBytesOfEntity('image', 'integration-test.png', 'image/png');

      cy.get(`[data-cy="isin"]`).type('calculating').should('have.value', 'calculating');

      cy.get(`[data-cy="dividendYield"]`).type('65321').should('have.value', '65321');

      cy.get(`[data-cy="sector"]`).type('navigating copying frictionless').should('have.value', 'navigating copying frictionless');

      cy.get(`[data-cy="industry"]`).type('Creative').should('have.value', 'Creative');

      // since cypress clicks submit too fast before the blob fields are validated
      cy.wait(200); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        stockInfo = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', stockInfoPageUrlPattern);
    });
  });
});
